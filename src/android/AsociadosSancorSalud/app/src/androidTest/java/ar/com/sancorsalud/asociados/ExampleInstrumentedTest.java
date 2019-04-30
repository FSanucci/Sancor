package ar.com.sancorsalud.asociados;

import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.concurrent.CountDownLatch;

import ar.com.sancorsalud.asociados.model.user.User;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HRestEngine;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;


public class ExampleInstrumentedTest extends ApplicationTestCase<AppController> {

    private static final String TAG = "TEST";

    HRestEngine restEngine;
    final CountDownLatch signal = new CountDownLatch(1);

    public ExampleInstrumentedTest() {
        super(AppController.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        restEngine = getApplication().getInstance().getRestEngine();
    }

    @LargeTest
    public void testRestEngine() {
        // The restEngine should be initializated
        assertNotNull(restEngine);
        //assertEquals(false, restEngine==null);
    }

    @LargeTest
    public void testGetPromociones() {
        HRequest request = RestApiServices.createLoginRequest("referente","1234",new Response.Listener<User>() {
            @Override
            public void onResponse(User user) {
                Log.e("Login","Successs");
                assertTrue(true);
                signal.countDown();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(AppController.getInstance(),"Error",Toast.LENGTH_SHORT);
                Log.e("Login","Failed");
                assertTrue(false);
                signal.countDown();
            }
        });

        restEngine.addToRequestQueue(request,false);
        try {
            signal.await();
        }catch (Exception e){
            assertTrue(false);
        }
    }

}