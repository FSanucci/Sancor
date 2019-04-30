package ar.com.sancorsalud.asociados.rest.services;


public class RestConstants {

    public static final int SOCKET_TIMEOUT_MS = 15000; // 15s

    public static final int LOGIN_TIMEOUT_MS = 30000; // 30s
    public static final int LIST_TIMEOUT_MS = 80000; // 80s
    public static final int QUOTATION_TIMEOUT_MS = 10 * 60 * 1000; // 10 min

    public static final String API_KEY = "api_key";

    public static final String P12_PASSWORD = "g0Ah3uNi4MqPQ7NSRO3BSbKD38dmvJ65xhv3U";
    //public static final String CERTIFICATE_NAME = "Cuoma_Desarrollo.p12";

    // DATA CENTER TEST BASE HOST
    //public static final String BASE_HOST = "https://serviciostestdc.sancorsalud.com.ar/";

    // TEST BASE HOST
    public static final String BASE_HOST = "https://testservicios.sancorsalud.com.ar/";

    //  TEST BASE CERTIFICATE NODE
    //public static final String BASE_HOST = "https://testserviciosmobile.sancorsalud.com.ar/";

    // PRODUCTION BASE  HOST
    //public static final String BASE_HOST = "https://servicios.sancorsalud.com.ar/";

    //  PRODUCTION CERTIFICATE NODE
    //public static final String BASE_HOST = "https://serviciosmobile.sancorsalud.com.ar/";

    // HOST
    public static final String HOST = BASE_HOST + "asociados/api/";
    public static final String HOST_USER = BASE_HOST + "Seguridad/webresources/ServicioUsuario/";
    public static final String WEB_HOST = BASE_HOST + "SancorSalud/webresources/";
    public static final String HOST_DIGITALIZATION = BASE_HOST + "Digitalizacion/api/";

    public static final String LOGIN_SERVICE = "login";
    public static final String IS_LOGGED_IN = "IsLoggedIn";

    public static final String APP_ID_FOR_AMAZON = "1";

    //Agenda
    public static final String CREATE_APPOINTMENT = "PotencialAsociado_Agendar";

    public static final String GET_CLOSE_REASONS = "PotencialAsociadoMotivos_Mostrar/";
    public static final String GET_SALESMANS = "PotencialAsociado_MostrarPromotores";
    public static final String ASSIGN_SALESMAN = "PotencialAsociado_AsignarPromotor";
    public static final String POST_SALESMANS_BY_ZONE = "Promotores_Listar";

    //Prospective Client
    public static final String CLOSE_PROSPECTIVE_CLIENT = "PotencialAsociado_Cerrar";
    public static final String GET_PROSPECTIVE_CLIENT_PROFILE = "PotencialAsociado_Perfil/";
    public static final String GET_ALL_PROSPECTIVE_CLIENT = "PotencialAsociado_ListaParaReferentes";
    public static final String GET_ALL_PROSPECTIVE_CLIENT_BY_STATE = "PotencialAsociado_ListaParaPromotores/estado/";

    public static final String GET_MY_PROSPECTIVE_CLIENT = "PotencialAsociado_ListaParaPromotores";

    public static final String GET_PAGING_PA_LIST = "PotencialAsociado_ListaParaPromotores/V2";

    public static final String CHECK_PROSPECTIVE_CLIENT = "PotencialAsociado_VerificarExistencia/";
    public static final String GET_COUNTERS = "PotencialAsociado_CantidadAsignadosyPendientesPromotor/";
    public static final String GET_ZONELEADER_COUNTERS = "PotencialAsociado_CantidadAsignadosyPendientes/";
    public static final String ADD_PROSPECTIVE_CLIENT = "PotencialAsociado_Agregar";
    public static final String EDIT_PROSPECTIVE_CLIENT = "PotencialAsociado_Modificar";
    public static final String GET_ZONES = "PotencialAsociadoZonas_Listar/";
    public static final String PUT_QUOTE = "PotencialAsociado_Cotizar/";


    public static final String GET_NOTIFICATIONS = "Notificaciones/";    // added user id
    public static final String GET_NOTIFICATIONS_SENDED = "Notificaciones/enviadas/";    // added user id


    public static final String GET_NOTIFICATIONS_COUNTERS = "Notificaciones/";
    public static final String PUT_NOTIFICATIONS_MARK_AS_READ = "Notificaciones/Leidos/";
    public static final String PUT_REMOVE_NOTIFICATION = "Notificaciones/Eliminar/";
    public static final String POST_SEND_NOTIFICATION = "Notificaciones/Enviar/";
    public static final String POST_ADD_NOTIFICATION = "Notificaciones/Agregar/";

    public static final String GET_MODULO_BASE = "CotizacionModuloBase_Listar";
    public static final String GET_BANCOS = "Cotizacion_Listar/Bancos";
    public static final String GET_FORMASPAGO = "Cotizacion_Listar/FormasDePago";
    public static final String GET_TARJETAS = "Cotizacion_Listar/Tarjetas";
    public static final String GET_OSDESREGULA = "Cotizacion_Listar/OSDesregula";

    public static final String GET_SEARCH_EMPRESA = "Cotizacion_Listar/Empresas/";
    public static final String GET_SEARCH_AFINIDAD = "Cotizacion_Listar/Afinidades/";
    public static final String GET_SEARCH_ENTITY = "CotizacionEntidades_Listar/";
    public static final String GET_SEARCH_DATERO = "ServicioDatero?";
    public static final String POST_SEARCH_LOCALIDAD = "ServicioLocalidad/autocompletar";

    public static final String GET_DES_NUMBER_VERIFICATION = "ServicioFichas/Verificar_Numero_DES/";
    public static final String GET_DES_VALIDATION = "ServicioFichas/ValidarDes/";

    public static final String POST_QUOTATION = "PotencialAsociado_Cotizar";
    public static final String POST_SAVE_QUOTATION = "PotencialAsociado_Cotizar/Guardar";
    public static final String GET_ADICIONALES_OPTATIVOS = "CotizacionAdicionales_Listar";
    public static final String POST_REQUOTATION = "PotencialAsociado_Cotizar/Consultar_Valor";


    public static final String POST_LISTAR_COTIZADOS = "PotencialAsociado_Cotizar/Listar";
    public static final String POST_LISTAR_INTEGRANTES = "Grupo_ListarPlanesIntegrantes";

    public static final String SAVE_SELECTED_PLAN = "PotencialAsociado_Cotizar/Guardar_Plan";

    public static final String POST_ADD_FILE = "ServicioFichas/Alta_Archivo";
    public static final String POST_GET_FILE = "ServicioFichas/Buscar_Archivo";
    public static final String POST_GET_FILE_IMAGE = "ServicioFichas/Buscar_Archivo_Imagen";


    public static final String POST_ADD_AMAZON_FILE = "FilesAws";
    public static final String DELETE_AMAZON_FILE = "FilesAws";

    public static final String GET_LINK_DATA = "ServicioFichas/Links/";    // add link Id


    public static final String POST_REMOVE_FILE = "ServicioFichas/Eliminar_Archivo";

    public static final String POST_ATTACH_FILE = "ServicioFichas/Des_AdjuntarDocumento";
    public static final String POST_ATTACH_REMOVE_FILE = "ServicioFichas/Des_EliminarDocumento";

    public static final String POST_DES_GET = "ServicioFichas/Des_Obtener";
    public static final String POST_DES_SAVE = "ServicioFichas/Des_Guardar";
    public static final String PUT_DES_UPDATE = "ServicioFichas/Des_Guardar";

    public static final String POST_DES_CHECK_STATE = "ServicioFichas/Des_Estado";
    public static final String POST_DES_AUDITORIA = "ServicioFichas/Derivar_Des";
    public static final String PUT_DES_AUDITORIA = "ServicioFichas/Derivar_Des";


    public static final String POST_EE_LIST = "ServicioFichas/Ficha_Listar_Empresa_Laboral";

    public static final String POST_CARD_GET = "ServicioFichas/Ficha_Obtener";
    public static final String POST_CARD_SAVE = "ServicioFichas/Ficha_Guardar";
    public static final String PUT_CARD_UPDATE = "ServicioFichas/Ficha_Guardar";

    public static final String CHANGE_STATE_CARD = "ServicioFichas/Ficha_Cambiar_Estado";
    public static final String POST_VERIFY_CREDIT_CARD = "ServicioFichas/Ficha_Tarjeta_Credito_Verificar";
    public static final String POST_VERIFY_TICKET_PAGO_ANTICIPADO = "ServicioFichas/Ficha_Pago_Anticipado_Verificar";

    public static final String POST_VERIFY_AUTHORIZATION_CARD = "ServicioFichas/Ficha_Autorizacion_Pendientes_Verificar";
    public static final String POST_VERIFY_ALTA_INMEDIATA = "ServicioFichas/Alta_Inmediata_Verificar";

    public static final String POST_FORMA_PAGO_ADD = "ServicioFichas/Ficha_Agregar_Forma_Pago";
    public static final String PUT_FORMA_PAGO_UPDATE = "ServicioFichas/Ficha_Agregar_Forma_Pago";

    public static final String POST_ADD_ENTIDAD_EMPLEADORA = "ServicioFichas/Ficha_Agregar_Empresa_Laboral";
    public static final String POST_ADD_OBRA_SOCIAL = "ServicioFichas/Ficha_ObraSocial_Guardar";

    // WORKFLOW Promotion
    public static final String POST_VERIFICATION_AUTHORIZATION_PROMO = "ServicioFichas/Ficha_Autorizacion_Promocion_Verificar";
    public static final String GET_AUTHORIZATION_PROMOTION = "ServicioAutorizacion/Ficha_Autorizacion_Promocion_Obtener";
    public static final String SAVE_AUTHORIZATION_PROMOTION = "ServicioAutorizacion/Ficha_Autorizacion_Promocion_Guardar";
    public static final String UPDATE_AUTHORIZATION_PROMOTION = "ServicioAutorizacion/Ficha_Autorizacion_Promocion_Modificar";

    // WORKFLOW Cobranzas
    public static final String GET_VERIFICATION_MOROSIDAD = "Cotizacion_Listar/VerificarEmpresa/";
    public static final String POST_VERIFICATION_AUTHORIZATION_COBRZ = "ServicioFichas/Ficha_Autorizacion_Cobranza_Verificar";

    public static final String GET_AUTHORIZATION_COBRANZA = "ServicioAutorizacion/Ficha_Autorizacion_Cobranza_Obtener";
    public static final String SAVE_AUTHORIZATION_COBRANZA = "ServicioAutorizacion/Ficha_Autorizacion_Cobranza_Guardar";
    public static final String UPDATE_AUTHORIZATION_COBRANZA = "ServicioAutorizacion/Ficha_Autorizacion_Cobranza_Modificar";

    public static final String GET_DEFAULT_CAR = "Usuario_ObtenerCarPredeterminadoSistemaSuass?idUsuario=";
    public static final String POST_DEFAULT_CAR = "Usuario_ActualizarCarPredeterminadoSistemaSuass";
    public static final String GET_CARS = "ServicioFichas/Car_Listar";

    //Indicators
    //public static final String GET_SALESMAN_INDICATOR = "Indicadores_Obtener/Promotor"; url vieja
    public static final String GET_SALESMAN_INDICATOR = "ServicioIndicadoresPromotor?";

    //public static final String GET_ZONELEADER_INDICATORS = "Indicadores_Obtener/Supervisor"; url vieja
    public static final String GET_ZONELEADER_INDICATORS = "ServicioIndicadoresReferente?";


    //Subte
    public static final String GET_GRAV = "PotencialAsociado_Trazabilidad/Grav";
    public static final String GET_NOGRAV = "PotencialAsociado_Trazabilidad/NoGrav";

    public static final String POST_AFFINITY_DOCUMENT = "ServicioFichas/Documento_Afinidad";
    public static final String GET_AFFINITY_MESSAGE = "ServicioGrupoAfinidad/documentoMensaje/";

    public static final String POST_AGREEMENT_DOCUMENT = "ServicioFichas/Documento_Convenio";
    public static final String GET_AGREEMENT_MESSAGE = "ServicioConvenio/documentoMensaje/";
}

