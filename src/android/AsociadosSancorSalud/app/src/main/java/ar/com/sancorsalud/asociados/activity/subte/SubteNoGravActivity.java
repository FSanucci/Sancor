package ar.com.sancorsalud.asociados.activity.subte;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Response;
import java.util.HashMap;
import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.Station;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;

public class SubteNoGravActivity extends SubteActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subte_no_grav);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.subte);

        mMainContainer = findViewById(R.id.main);

        mProgressView = (ProgressBar) findViewById(R.id.progress);
        mMainContainer = findViewById(R.id.main_container);

        this.stationsName = new HashMap<String, String>();
        this.stationsView = new HashMap<String, View>();

        View comienzoStation = findViewById(R.id.comienzo);
        this.stationsView.put("comienzo",comienzoStation);
        this.stationsName.put("comienzo","Comienzo");

        View asignadoReferenteStation = findViewById(R.id.asignado_referente);
        this.stationsView.put("asignado_referente",asignadoReferenteStation);
        this.stationsName.put("asignado_referente","Asignado a referente de zona");

        View asignadoPromotorStation = findViewById(R.id.asignado_promotor);
        this.stationsView.put("asignado_promotor",asignadoPromotorStation);
        this.stationsName.put("asignado_promotor","Asignado a asesor comercial");

        View cotizadoStation = findViewById(R.id.cotizado);
        this.stationsView.put("cotizado",cotizadoStation);
        this.stationsName.put("cotizado","Cotizado");

        View cargaProcesoStation = findViewById(R.id.carga_proceso);
        this.stationsView.put("carga_proceso",cargaProcesoStation);
        this.stationsName.put("carga_proceso","Carga en proceso");

        View enviadoAprobacionStation = findViewById(R.id.enviado_aprobacion);
        this.stationsView.put("enviado_aprobacion",enviadoAprobacionStation);
        this.stationsName.put("enviado_aprobacion","Enviado a aprobación");

        View cerradoStation = findViewById(R.id.cerrado);
        this.stationsView.put("cerrado",cerradoStation);
        this.stationsName.put("cerrado","Cerrado");

        View fin1Station = findViewById(R.id.fin1);
        this.stationsView.put("fin1",fin1Station);
        this.stationsName.put("fin1","Fin");

        View altaProvisoriaStation = findViewById(R.id.alta_provisoria);
        this.stationsView.put("alta_provisoria",altaProvisoriaStation);
        this.stationsName.put("alta_provisoria","Alta Provisoria");

        View consistenciaInconsistenciaStation = findViewById(R.id.consistencia_inconsistencia);
        this.stationsView.put("consistencia_inconsistencia",consistenciaInconsistenciaStation);
        this.stationsName.put("consistencia_inconsistencia","Consistencia/Inconsistencia");

        View altaDefinitivaStation = findViewById(R.id.alta_definitiva);
        this.stationsView.put("alta_definitiva",altaDefinitivaStation);
        this.stationsName.put("alta_definitiva","Alta definitiva");

        View credencialEntregadaStation = findViewById(R.id.credencial_entregada);
        this.stationsView.put("credencial_entregada",credencialEntregadaStation);
        this.stationsName.put("credencial_entregada","Estados de credencial");

        View fin2Station = findViewById(R.id.fin2);
        this.stationsView.put("fin2",fin2Station);
        this.stationsName.put("fin2","Fin");

        View pendienteEnvioCarStation = findViewById(R.id.pendiente_envio_car);
        this.stationsView.put("pendiente_envio_car",pendienteEnvioCarStation);
        this.stationsName.put("pendiente_envio_car","Pendiente de envío a CAR");

        View enviadaAprobacionCarStation = findViewById(R.id.enviada_aprobacion_car);
        this.stationsView.put("enviada_aprobacion_car",enviadaAprobacionCarStation);
        this.stationsName.put("enviada_aprobacion_car","Enviada para aprobación a CAR");

        View derivadaSoportePromocionStation = findViewById(R.id.derivada_soporte_promocion);
        this.stationsView.put("derivada_soporte_promocion",derivadaSoportePromocionStation);
        this.stationsName.put("derivada_soporte_promocion","Derivada a Soporte Promoción");

        View enviadaArchivoStation = findViewById(R.id.enviada_archivo);
        this.stationsView.put("enviada_archivo",enviadaArchivoStation);
        this.stationsName.put("enviada_archivo","Enviada a Archivo");

        View archivadaStation = findViewById(R.id.archivada);
        this.stationsView.put("archivada",archivadaStation);
        this.stationsName.put("archivada","Archivada");

        View finStation = findViewById(R.id.fin);
        this.stationsView.put("fin",finStation);
        this.stationsName.put("fin","Fin");

        getContent();
    }


    public HRequest createService(Response.Listener<HashMap<String,Station>> listener, Response.ErrorListener errorListener){
        return RestApiServices.createGetNoGravRequest(mProspectiveClientId,listener,errorListener);
    }
}
