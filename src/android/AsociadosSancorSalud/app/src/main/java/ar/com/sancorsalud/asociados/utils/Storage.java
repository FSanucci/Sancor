package ar.com.sancorsalud.asociados.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class Storage {

    private static final String TAG = "Storage";
    private static final String SHARED_PREFERENCES = "sancord_v1";
    private static final String OLD_SHARED_PREFERENCES = "sancord_v0";

    //private Context ctx;
    private SharedPreferences sharedPreferences;
    private static Storage instance;

    private boolean hasChangePAQuantityList = false;

    private boolean hasStartedSalesmanBadgeRequestCycle = false;
    private boolean hasStartedZoneLeaderBadgeRequestCycle = false;

    private Long affiliationCardId = null;
    private boolean permissionFlag = false;

    private boolean cardEditableMode = true;
    private boolean hasSendNotification = false;

    private String entidadEmpladoraKey = "ENTIDAD_EMPLEADORA_KEY";


    private boolean reloadPA = false;

    private int totalListItem = 0;

    public static synchronized Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    public void init(Context ctx) {
        //this.ctx = ctx;
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
            clearOldSharedPreferenceIfNeeded(ctx);
        }
    }


    public String getPreferences(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void putPreferences(String key, String value) {
        if (key != null && key.trim().length() > 0 && value != null && value.trim().length() > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public void clearOldSharedPreferenceIfNeeded(Context ctx) {
        if (getBoolPreferences(OLD_SHARED_PREFERENCES)) {
            SharedPreferences sp = ctx.getSharedPreferences(OLD_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.commit();
            putBoolPreferences(OLD_SHARED_PREFERENCES, false);
        }
    }

    public long getLongPreferences(String key) {
        return sharedPreferences.getLong(key, 0);
    }

    public void putLongPreferences(String key, long value) {
        if (key != null && key.trim().length() > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(key, value);
            editor.commit();
        }
    }

    public boolean getBoolPreferences(String key) {
        return sharedPreferences.getBoolean(key, true);
    }

    public void putBoolPreferences(String key, boolean value) {
        if (key != null && key.trim().length() > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    public void remove(String key) {
        if (key != null && key.trim().length() > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.commit();
        }
    }

    public boolean hasChangePAQuantityList() {
        return hasChangePAQuantityList;
    }

    public void setHasChangePAQuantityList(boolean hasChangePAQuantityList) {
        this.hasChangePAQuantityList = hasChangePAQuantityList;
    }


    public boolean hasStartedSalesmanBadgeRequestCycle() {
        return hasStartedSalesmanBadgeRequestCycle;
    }

    public void setHasStartedSalesmanBadgeRequestCycle(boolean hasStartedSalesmanBadgeRequestCycle) {
        this.hasStartedSalesmanBadgeRequestCycle = hasStartedSalesmanBadgeRequestCycle;
    }


    public boolean hasStartedZoneLeaderBadgeRequestCycle() {
        return hasStartedSalesmanBadgeRequestCycle;
    }

    public void setHasStartedZoneLeaderBadgeRequestCycle(boolean hasStartedSalesmanBadgeRequestCycle) {
        this.hasStartedSalesmanBadgeRequestCycle = hasStartedSalesmanBadgeRequestCycle;
    }

    public Long getAffiliationCardId() {
        return affiliationCardId;
    }

    public void setAffiliationCardId(Long affiliationCardId) {
        this.affiliationCardId = affiliationCardId;
    }


    public boolean hasSendNotification() {
        return hasSendNotification;
    }

    public void setHasSendNotification(boolean hasSendNotification) {
        this.hasSendNotification = hasSendNotification;
    }


    public boolean hasPermissionFlag() {
        return permissionFlag;
    }

    public void setPermissionFlag(boolean permissionFlag) {
        this.permissionFlag = permissionFlag;
    }

    public boolean isCardEditableMode() {
        return cardEditableMode;
    }

    public void setCardEditableMode(boolean cardEditableMode) {
        this.cardEditableMode = cardEditableMode;
    }


    public boolean hasToReloadPA() {
        return reloadPA;
    }

    public void setReloadPA(boolean reloadPA) {
        this.reloadPA = reloadPA;
    }

    public int getTotalListItem() {
        return totalListItem;
    }

    public void setTotalListItem(int totalListItem) {
        this.totalListItem = totalListItem;
    }

    public void setEntidadEmpleadoraAvailable(boolean value) {
        putBoolPreferences(entidadEmpladoraKey, value);
    }

    public boolean getEntidadEmpleadoraAvailable() {
        return getBoolPreferences(entidadEmpladoraKey);
    }

}
