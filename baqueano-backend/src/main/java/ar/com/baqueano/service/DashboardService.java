package ar.com.baqueano.service;

import ar.com.baqueano.dto.dashboard.DashboardResumenDTO;

public interface DashboardService {

    /** Totales para las stat cards y la tabla de ultimos contactos del dashboard. */
    DashboardResumenDTO obtenerResumen();
}
