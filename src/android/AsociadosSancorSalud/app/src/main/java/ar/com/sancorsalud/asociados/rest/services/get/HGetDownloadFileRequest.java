package ar.com.sancorsalud.asociados.rest.services.get;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;


public class HGetDownloadFileRequest extends HRequest<File> {

    private File outputFile;

    public HGetDownloadFileRequest(String pathFile, File outputFile, Response.Listener<File> listener, Response.ErrorListener errorListener) {
        super(Method.GET, pathFile, listener, errorListener);
        setShouldCache(false);
        this.outputFile=outputFile;
    }

    @Override
    protected Response<File> parseNetworkResponse(NetworkResponse response) {
        if (responseHaveRequestError(response))
            return Response.error(new VolleyError(response.statusCode + " | Request failed"));

        try{
            int count;
            InputStream input = new ByteArrayInputStream(response.data);
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            return Response.success(outputFile, HttpHeaderParser.parseCacheHeaders(response));
        }catch(IOException e){
            e.printStackTrace();

        }

        return Response.error(new VolleyError("Can't save file"));

    }

    @Override
    public String getBodyContentType() {
        return null;
    }

    @Override
    protected File parseObject(Object obj) {
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return null;
    }

}