# ============================================================
# Instala el driver MySQL como modulo de WildFly.
#
# Detecta automaticamente la version mas alta de mysql-connector-j
# disponible en el repo Maven local (~/.m2) y genera el module.xml
# de WildFly apuntando a ese jar. Esto evita hardcodear una version
# que puede cambiar segun lo que Spring Boot resuelva del BOM.
#
# Uso:
#   .\scripts\wildfly\setup-mysql-module.ps1
#
# Para forzar una version puntual:
#   .\scripts\wildfly\setup-mysql-module.ps1 -DriverVersion 9.7.0
# ============================================================

param(
    [string]$WildflyHome = $env:WILDFLY_HOME,
    [string]$DriverVersion = ""
)

if (-not $WildflyHome) {
    Write-Error "WILDFLY_HOME no esta seteado y no se paso por parametro."
    exit 1
}

$ConnectorBase = Join-Path $env:USERPROFILE ".m2\repository\com\mysql\mysql-connector-j"

if (-not (Test-Path $ConnectorBase)) {
    Write-Error "No se encuentra mysql-connector-j en el repo Maven local: $ConnectorBase"
    Write-Host "Tip: corre 'mvn -pl baqueano-backend dependency:resolve' para descargarlo."
    exit 1
}

# Si no se especifico version, tomamos la mas alta presente en .m2
if (-not $DriverVersion) {
    $LatestDir = Get-ChildItem -Path $ConnectorBase -Directory |
        Sort-Object { [version]$_.Name } -Descending |
        Select-Object -First 1
    if (-not $LatestDir) {
        Write-Error "No hay subcarpetas de version en $ConnectorBase"
        exit 1
    }
    $DriverVersion = $LatestDir.Name
    Write-Host "Detectada version: $DriverVersion (usar -DriverVersion para forzar otra)"
}

$DriverJar = "mysql-connector-j-$DriverVersion.jar"
$MavenLocal = Join-Path $ConnectorBase "$DriverVersion\$DriverJar"

if (-not (Test-Path $MavenLocal)) {
    Write-Error "No se encuentra el jar: $MavenLocal"
    Write-Host "Versiones disponibles en .m2:"
    Get-ChildItem -Path $ConnectorBase -Directory | Select-Object Name | Format-Table -HideTableHeaders
    exit 1
}

$ModuleDir = Join-Path $WildflyHome "modules\com\mysql\main"
New-Item -ItemType Directory -Force -Path $ModuleDir | Out-Null

# Copiar el jar al modulo de WildFly
Copy-Item -Path $MavenLocal -Destination (Join-Path $ModuleDir $DriverJar) -Force

# Generar module.xml apuntando al jar exacto (sin depender de un template estatico)
$ModuleXml = @"
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="com.mysql">
    <resources>
        <resource-root path="$DriverJar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
"@

Set-Content -Path (Join-Path $ModuleDir "module.xml") -Value $ModuleXml -Encoding UTF8

Write-Host ""
Write-Host "Modulo com.mysql instalado en: $ModuleDir"
Write-Host "Contenido:"
Get-ChildItem $ModuleDir | Format-Table Name, Length
Write-Host ""
Write-Host "Siguiente paso: con WildFly corriendo, ejecuta:"
Write-Host "  & `"$WildflyHome\bin\jboss-cli.bat`" --connect --file=scripts\wildfly\wildfly-datasource.cli"
