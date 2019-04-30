package ar.com.sancorsalud.asociados.model;

import java.io.Serializable;

import ar.com.sancorsalud.asociados.utils.ConstantsUtil;


public class AttachFile implements Serializable {
    public long id = -1L;

    public String subDir;
    public String filePath;
    public String fileName;
    public String fileExtension;
    public String fileNameAndExtension;

    public String status = ConstantsUtil.UNSYNC_STATE;

}