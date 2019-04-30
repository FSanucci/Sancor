var Config = {
  api : "https://testservicios.sancorsalud.com.ar/asociados/api/",
  apiAuthorization : "121212121221",
  roleNameSupervisor : "referente_zona",
  roleNamePromoter : "promotor",
  roleNameDataEntry: "datero",
  maxDaysPotentialCanBeAssigned : 30,
  refreshNotificationsMilliseconds : 10000,
  events : {
    onHeaderSearchTextChange : "onHeaderSearchTextChange",
    onHeaderNavigatePageChange : "onHeaderNavigatePageChange",
  },
  publicPages : {
    agenda : "/agenda",
    about : "/acerca",
    login : "/inicio",
    newAssociatedPotential : "/nuevo",
    assignedToPromoter : "/asignados",
    promoterList : "/asesores",
    toBeAssigned : "/asignaciones",
    onlyAdditional: "/solo-adicionales",
    transfer: "/transferencia-segmento",
    notifications: "/notificaciones",
    valuationPlan: "/cotizar-plan",
  },
  card : {
    national : "N",
    local : "R"
  },
  affinityGroups : {
    cbuOrNationalCard : 723
  },
  wayToPay:{
    creditCard : 'TC ',
    cbu : 'CBU',
    emp : 'EMP'
  },
  segments:{
    autonomo : 'autonomo'
  } ,
  formaDeIngreso:{
    empresa : 'empresa',
    empresaNoExistenteLeyenda : "Si la empresa no aparece, puede estar con convenio pendiente de firma. Deberá cargar empresa con nro: XXXXXX, para que ud. pueda continuar con la cotización."
  }


};
