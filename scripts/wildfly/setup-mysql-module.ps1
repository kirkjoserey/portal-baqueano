# ============================================================
# Instala el driver MySQL como modulo de WildFly.
#
# Lee WILDFLY_HOME de la variable de entorno (o lo recibe como parametro).
# Copia mysql-connector-j-9.3.0.jar del repositorio Maven local a
# $WILDFLY_HOME\modules\com\mysql\main\ y deja ahi el module.xml.
#
# Uso:
#   .\scripts\wildfly\setup-mysql-module.ps1
# ============================================================

param(
    [string]$WildflyHome = $env:WILDFLY_HOME,
    [string]$DriverVersion = "9.3.0"
)

if (-not $WildflyHome) {
    Write-Error "WILDFLY_HOME no esta seteado y no se paso por parametro."
    exit 1
}

$ModuleDir = Join-Path $WildflyHome "modules\com\mysql\main"
$DriverJar = "mysql-connector-j-$DriverVersion.jar"
$MavenLocal = Join-Path $env:USERPROFILE ".m2\repository\com\mysql\mysql-connector-j\$DriverVersion\$DriverJar"
$ScriptDir = $PSScriptRoot

if (-not (Test-Path $MavenLocal)) {
    Write-Error "No se encuentra el driver en el repo Maven local: $MavenLocal"
    Write-Host "Tip: corre 'mvn -pl baqueano-backend dependency:resolve' para descargarlo."
    exit 1
}

New-Item -ItemType Directory -Force -Path $ModuleDir | Out-Null

Copy-Item -Path $MavenLocal -Destination (Join-Path $ModuleDir $DriverJar) -Force
Copy-Item -Path (Join-Path $ScriptDir "module.xml") -Destination (Join-Path $ModuleDir "module.xml") -Force

Write-Host "Modulo com.mysql instalado en: $ModuleDir"
Write-Host "Contenido:"
Get-ChildItem $ModuleDir | Format-Table Name, Length
Write-Host ""
Write-Host "Siguiente paso: con WildFly corriendo, ejecuta:"
Write-Host "  & `"$WildflyHome\bin\jboss-cli.bat`" --connect --file=scripts\wildfly\wildfly-datasource.cli"
