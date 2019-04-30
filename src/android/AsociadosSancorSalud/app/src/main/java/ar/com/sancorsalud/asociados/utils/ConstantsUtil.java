package ar.com.sancorsalud.asociados.utils;


public class ConstantsUtil {

    public static final int DISTANCE_TO_TRIGGER_SYNC_LIST = 350;
    public static long FREE_MEORY_DELAY = 3 * 60000L;
    public static long BADGE_COUNT_REFRESH_DELAY = 5 * 60000L;


    public static final String STAFF_ABACOM = "ABACOM";

    // Broadcast actions
    public static final String REFRESH_SALESMAN_ASSIGMENT_BADGE = "ar.com.sancorsalud.salesman.assigment.badge";
    public static final String REFRESH_NOTIFICATION_BADGE = "ar.com.sancorsalud.refresh_notif.badge";
    public static final String REFRESH_ZONE_LEADER_ASSIGMENT_BADGE = "ar.com.sancorsalud.zoneleader.assigment.badge";

    public static final int CALENDAR_ID = 1;

    public static final String CREATE_MODE = "CREATE_MODE";
    public static final String ADD_MODE = "ADD_MODE";

    public static final String BADGE_COUNT = "BADGE_COUNT";
    public static final String QRCODE_DATA = "QRCODE_DATA";

    public static final String SELECTED_CLIENTS = "selected_clients";
    public static final String ASSIGN_PROCESS = "ASSIGN_PROCESS";

    public static final String ARG = "arg";
    public static final String CLIENT_ARG = "client";
    public static final String CLIENT_READ_ONLY = "readOnly";
    public static final String WORK_CARGA = "forCarga";


    public static final String RELOAD_DATA = "reloadData";
    public static final String CARDS_IN_PROCESS = "inProcess";


    public static final String IS_ZONE_LEADER_ROLE = "isLeaderRole";
    public static final String NOTIFICATION_ID = "notifId";

    public static final String QUOTE_TYPE_GENERAL = "quoteGeneral";
    public static final String QUOTE_TYPE_MANUAL = "quoteManual";

    public static final String RESULT_MEMBER = "104";
    public static final String RESULT_APORTE = "105";
    public static final String RESULT_EE = "106";
    public static final String RESULT_CONYUGE_DATA = "107";
    public static final String UPDATE_CONYUGE_MONO_FILES = "CONYUE_MONO_FILES";

    public static final String SELECTED_FILTER = "SELECTED_FILTER";
    public static final String SELECTED_STATE = "SELECTED_STATE";

    public static final int SELECT_FILTER_OR_STATE = 1;
    public static final int VIEW_CARD_DETAIL = 2;
    public static final int CHANGED_STATE = 3;

    public static final int RESULT_DATA_UPDATED = 2;
    public static final int VIEW_DETAIL_REQUEST_CODE = 3;
    public static final int CALENDAR_REQUEST_CODE = 4;
    public static final int VIEW_SALESMAN_SELECTION_REQUEST_CODE = 5;
    public static final int VIEW_EDIT_PROFILE_REQUEST_CODE = 6;
    public static final int CLIENT_CODE = 7;
    public static final int VIEW_CODE = 8;
    public static final int VIEW_EE = 9;
    public static final int VIEW_CONYUGE_OS = 10;
    public static final int VIEW_NOTIFICATION = 11;

    public static final int VIEW_AUTH_PROMO = 12;
    public static final int VIEW_AUTH_COBRZ = 13;

    public static final int SALESMAN_ID = 11;
    public static final int ZONE_LEADER_ID = 12;
    public static final int PROMOTION_SUPPORT_ID = 31;

    public static final String RESULT_PA = "client";

    public static String RESULT_AUTH_COBRZ = "AuthCobr";

    public enum Segmento {
        AUTONOMO, DESREGULADO, MONOTRIBUTO;
    }

    public enum FormaIngreso {
        INDIVIDUAL, AFINIDAD, EMPRESA;
    }

    public static final String ZERO_SEGMENTO = "0";
    public static final String AUTONOMO_SEGMENTO = "1";
    public static final String DESREGULADO_SEGMENTO = "2";
    public static final String MONOTRIBUTO_SEGMENTO = "3";


    public static final String INDIVIDUAL_FORMA_INGRESO = "1";
    public static final String EMPRESA_FORMA_INGRESO = "2";
    public static final String AFINIDAD_FORMA_INGRESO = "3";

    public static final String TARJETA_CREDITO_FORMA_PAGO = "TC";
    public static final String CBU_FORMA_PAGO = "CBU";
    public static final String PGF_FORMA_PAGO = "PGF";
    public static final String EF_FORMA_PAGO = "EF";
    public static final String COPAGO_ASOCIADO = "A";

    public static final long LOGIN_USER_ROLE_AMS = 2; //AMS = 2, Staff = 5
    public static final String AFFILIACION_COPAGO_ASOCIADO = "A";
    public static final String AFFILIACION_COPAGO_EMPRESA = "E";


    public static final String APORTE_LEGAL_OBRA_SOCIAL = "1";
    public static final String APORTE_LEGAL_REM_BRUTA = "2";

    public static final String TITULAR_MEMBER = "0";
    public static final String CONYUGE_MEMBER = "1";
    public static final String CONCUBINO_MEMBER = "2";
    public static final String HIJO_SOLT_MENOR_21_MEMBER = "3";
    public static final String HIJO_SOLT_21_25_MEMBER = "4";
    public static final String HIJO_CONY_SOLT_MENOR_21_MEMBER = "5";
    public static final String PARENTESCO_CODE_6 = "6";
    public static final String MENOR_TUTELA_MEMBER = "7";
    public static final String FAMILIAR_A_CARGO_MEMBER = "8";
    public static final String DISC_MAYOR_25_MEMBER = "9";

    public static final String TITULAR_MEMBER_DESC = "Titular";

    public static final String IMG_DIR = "img";
    public static final String DOWNLOAD_DIR = "download";
    public static final String IMG_EXTENSION = ".png";
    public static final String USER = "user";
    public static final String FILE_PROVIDER = "ar.com.sancorsalud.asociados.fileprovider";


    public static final String MONOTRIBUTO_REGIMEN = "2";
    public static final String NO_MONOTRIBUTO_REGIMEN = "1";
    public static final String CONDICION_IVA_CONSUMIDOR_FINAL = "3";

    public static final String ACTIVO_CATEGORIA = "1";
    public static final String ADHERENTE_CATEGORIA = "3";

    public static final String UNIFICA_APORTES = "UNIFICA_APORTES";
    public static final String FILTER_PARENTESCOS = "FILTER_PARENTESCOS";


    public static final String COTIZATION_CLIENT_MARCA = "C";
    //public static final String COTIZATION_TRANSFER_MARCA = "S";

    public static final String COTIZATION_MANUAL = "M";
    public static final String COTIZATION_TOTAL = "T";
    public static final String COTIZATION_PARCIAL = "P";

    public static final String COTIZATION_PLAN_SALUD = "S";
    public static final String COTIZATION_PLAN_OPCIONALES = "N";


    public static final String QUOTATION = "quotation";
    public static final String QUOTATION_RESULT = "quotationResult";
    public static final String OPTATIVOS_DATA = "optativosData";
    public static final String OPTATIVOS_HIDE_PLAN_VALUE = "hidePlanValue";

    public static final String DES = "des";
    public static final String AFFILIATION = "affiliation";
    public static final String PA_ID = "paId";
    public static final String LOAD_FROM_QR = "loadFromQR";

    public static final String AFFILIATION_TITULAR_DNI = "titularDNI";
    public static final String AFFILIATION_TITULAR_APORTA_MONO = "titularAportaMono";
    public static final String AFFILIATION_MEMBER = "member";
    public static final String AFFILIATION_MEMBER_INDEX = "index";
    public static final String AFFILIATION_ENTIDAD_EMPLEADORA = "EE";
    public static final String AFFILIATION_EE_INDEX = "EE_INDEX";

    public static final String OPCIONAL_TIPO_LISTA = "LISTA";
    public static final String OPCIONAL_TIPO_COMBO = "COMBO";

    public static final String QUOTED_DIR = "quoted";
    public static final String QUOTED_LIST_DIR = "quotedlist";

    public static final String QUOTED_ALL_PLANS = "T";      // Cotizaciones Totales
    public static final String QUOTED_PLANS_SAVED_FILTER = "G";      // Cotizaciones Guardadas
    public static final String QUOTED_PLAN_SELECTED_FILTER = "E";    // Cotizacion Elegida

    public static final String DOC_TYPE_IDENTITY = "96";

    public static final int ADDRESS_TITULAR_TYPE = 1;
    public static final int ADDRESS_ALTERNATIVE_TYPE = 2;


    public static final int DOC_ASSOCIATED_DNI_FILE = 1;
    public static final int DOC_ASSOCIATED_CUIL_FILE = 2;
    public static final int DOC_ASSOCIATED_COVERAGE = 5;
    public static final int DOC_ASSOCIATED_PLAN = 6;
    public static final int DOC_ASSOCIATED_ACTA_MATRIMONIO = 7;
    public static final int DOC_ASSOCIATED_PART_NACIMIENTO = 8;
    public static final int DOC_ASSOCIATED_IVA_FILE = 9;
    public static final int DOC_ASSOCIATED_FORM_184 = 10;
    public static final int DOC_ASSOCIATED_3_MONTH_TICKET = 11;
    public static final int DOC_ASSOCIATED_CERT_DISC_FILE = 12;
    public static final int DOC_ASSOCIATED_CONYUGE_FORM_184 = 13;
    public static final int DOC_ASSOCIATED_CONYUGE_3_MONTH_TICKET = 14;

    public static final String ARG_NATIONALITY_ID = "54";

    public static final String OS_STATE_NO_ACTIVE = "1";
    public static final String OS_STATE_OTHER_OS_OPTION = "2";
    public static final String OS_STATE_ACTIVE_AT_INTPUT = "3";
    public static final String OS_STATE_NO_CUIL_DATA = "4";
    public static final String OS_STATE_FINISH = "5";
    public static final String OS_STATE_ACTIVE = "6";

    public static final String OS_TYPE_SINDICAL = "S";
    public static final String OS_TYPE_DIRECCION = "D";

    public static final String OS_OSIM_ID = "39";
    public static final String OS_ASE_ID = "17";

    // TODO REMOVE OLD HARCODED LINKS
    public static final String OS_CODE_VERIFICATION_LINK = "https://www.sssalud.gob.ar/index.php?cat=consultas&page=busopc/";
    public static final String OS_ORIGIN_VERIFICATION_LINK = "https://www.sssalud.gob.ar/index.php?cat=consultas&page=busopc/";
    public static final String OS_AFIP_VERIFICATION_LINK = "https://serviciosweb.afip.gob.ar/TRAMITES_CON_CLAVE_FISCAL/MISAPORTES/app/basica/ingresoDatos.aspx/";
    public static final String ANSES_CUIL_LINK = "https://www.anses.gob.ar/constancia-de-cuil/";

    public static final String LINK_ID_OS_AFIP_VERIFICATION = "AFIP";
    public static final String LINK_ID_OS_CODE_VERIFICATION = "SSS";
    public static final String LINK_ID_ANSES_CUIL = "ANSES";


    public static final String LINK_TITLE = "title";
    public static final String LINK_URL = "url";
    public static final String ID = "id";

    public static final String AUTH_PROMOTION = "AUTH_PROMO";
    public static final String AUTH_COBRANZA = "AUTH_COBRZ";

    public static final int PENDING_TO_SEND_CAR_STATE = 4;
    public static final int SENT_TO_CAR_STATE = 6;
    public static final int PENDING_DOC_STATE = 8;

    public static final String FILE_INDEX_OS_CHANGE_OPTION = "opcion";
    public static final String FILE_INDEX_OS_FORM = "cartilla";
    public static final String FILE_INDEX_OS_CHANGE_CERT = "certificadoCambio";

    public static final String FILE_INDEX_OS_EMAIL = "email";
    public static final String FILE_INDEX_OS_FORM_53 = "form5.3";
    public static final String FILE_INDEX_OS_FORM_59 = "form5.9";
    public static final String FILE_INDEX_OS_MODEL_NOTE = "modelNote";
    public static final String FILE_INDEX_OS_CODE = "osCodigo";

    public static final String CIVIL_STATUS_SOLTERO = "S";

    public static final String ALTA_INMEDIATA = "I";
    public static final String ALTA_MES_HABIL = "P";

    public static final String FECHA_CARGA = "fechaCarga";
    public static final String CONYUGE_DATA = "conyugeData";

    public static final String UNSYNC_STATE = "UNSYNC";
    public static final String SYNC_STATE = "SYNC";

    public static final int DES_STATE_PENDING_CHARGE = -1;
    public static final int DES_STATE_PENDING = 0;
    public static final int DES_STATE_APROVED = 1;
    public static final int DES_STATE_REJECTED = 2;
    public static final int DES_STATE_IN_CORRECTION = 3;
    public static final int DES_STATE_APROVED_WITH_MODULE = 4;


    // WORKFLOW
    public static final int PROMOTION_VERIFICATION_STATE = 1;
    public static final int PROMOTION_APROVE_STATE = 2;
    public static final int PROMOTION_REJECTED_STATE = 3;
    public static final int PROMOTION_BACK_TO_COMERCIAL_STATE = 4;

    public static final int COBRANZA_VERIFICATION_STATE = 1;
    public static final int COBRANZA_APROVE_STATE = 2;
    public static final int COBRANZA_REJECTED_STATE = 3;
    public static final int COBRANZA_BACK_TO_COMERCIAL_STATE = 4;

}
