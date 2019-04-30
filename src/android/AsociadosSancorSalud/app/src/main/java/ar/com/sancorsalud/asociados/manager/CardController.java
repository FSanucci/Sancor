package ar.com.sancorsalud.asociados.manager;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.DecodedFile;
import ar.com.sancorsalud.asociados.model.LinkData;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCard;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCardInfo;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationDataResult;
import ar.com.sancorsalud.asociados.model.affiliation.Des;
import ar.com.sancorsalud.asociados.model.affiliation.DesNumberVerification;
import ar.com.sancorsalud.asociados.model.affiliation.DesResult;
import ar.com.sancorsalud.asociados.model.affiliation.DesState;
import ar.com.sancorsalud.asociados.model.affiliation.EntidadEmpleadora;
import ar.com.sancorsalud.asociados.model.affiliation.ObraSocial;
import ar.com.sancorsalud.asociados.model.affiliation.Pago;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

/**
 * Created by francisco on 18/7/17.
 */

public class CardController {

    private static final String TAG = "CARD_CONTR";

    public static final int FILE_TIMEOUT_MS = 2 * 60 * 1000; // 2 min

    private static CardController mInstance = new CardController();

    public static synchronized CardController getInstance() {
        return mInstance;
    }


    public void getDesData(final long desId, final long cardId, final Response.Listener<Des> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createGetDesRequest(desId, cardId, new Response.Listener<Des>() {
            @Override
            public void onResponse(Des desResult) {
                Log.e(TAG, "get DES  ok");
                listener.onResponse(desResult);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "get DES Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }

    public void verifyDesNumber(final long desNumber, final Response.Listener<DesNumberVerification> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.createGetDesNumberVerificationRequest(desNumber, new Response.Listener<DesNumberVerification>() {
            @Override
            public void onResponse(DesNumberVerification desNumberVerification) {
                Log.e(TAG, "get DES Number Validation  ok");
                listener.onResponse(desNumberVerification);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "get DES Number Validation Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }



    public void saveDesData(Des des, final Response.Listener<DesResult> listener, final Response.ErrorListener errorListener) {

        HRequest request = null;
        if (des.id == -1L) {
            request = RestApiServices.createSaveDesRequest(des, new Response.Listener<DesResult>() {
                @Override
                public void onResponse(DesResult desResut) {
                    Log.e(TAG, "create DES  ok");
                    listener.onResponse(desResut);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(TAG, "create DES Failed...");
                    errorListener.onErrorResponse(volleyError);
                }
            });
        } else {
            request = RestApiServices.createUpdateDesRequest(des, new Response.Listener<DesResult>() {
                @Override
                public void onResponse(DesResult desResut) {
                    Log.e(TAG, "update DES  ok");
                    listener.onResponse(desResut);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(TAG, "update DES Failed...");
                    errorListener.onErrorResponse(volleyError);
                }
            });
        }

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }


    public void checkDesState(long cardId, final Response.Listener<DesState> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.checkDesStateRequest(cardId, new Response.Listener<DesState>() {
            @Override
            public void onResponse(DesState desState) {
                Log.e(TAG, "check DES State  ok");
                listener.onResponse(desState);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "check DES State Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }

    public void sendDesToAuditoria(Des des, DesState desState, final Response.Listener<DesResult> listener, final Response.ErrorListener errorListener) {
        HRequest request;

        if (des.id != -1L && (desState!= null && desState.stateId == ConstantsUtil.DES_STATE_IN_CORRECTION)){
            request = RestApiServices.updateSendDesToAuditoriaRequest(des, new Response.Listener<DesResult>() {
                @Override
                public void onResponse(DesResult result) {
                    Log.e(TAG, "Update Auditoria data  ok");
                    listener.onResponse(result);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(TAG, "Update Auditoria Error...");
                    errorListener.onErrorResponse(volleyError);
                }
            });
        }else{
            request = RestApiServices.createSendDesToAuditoriaRequest(des, new Response.Listener<DesResult>() {
                @Override
                public void onResponse(DesResult result) {
                    Log.e(TAG, "Send Auditoria data  ok");
                    listener.onResponse(result);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(TAG, "Send Auditoria Error...");
                    errorListener.onErrorResponse(volleyError);
                }
            });

        }
        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }


    public void addFile(final AttachFile inputFile, String encodedImage, final Response.Listener<Long> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createAddFiledRequest(inputFile, encodedImage, new Response.Listener<Long>() {
            @Override
            public void onResponse(Long fileId) {
                Log.e(TAG, "add File  ok");
                listener.onResponse(fileId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "add File Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });


        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request, true, FILE_TIMEOUT_MS);
    }


    /*
    public void getFile(final Context ctx, final String subDir, final long resourceId, final Response.Listener<AttachFile> listener, final Response.ErrorListener errorListener) {

        Log.e(TAG, "GET FILE BY ID: " + resourceId);

        final Date d1 = new Date(System.currentTimeMillis());
        final long d1s = d1.getTime() / 1000;

        HRequest request = RestApiServices.createGetFileRequest(resourceId, new Response.Listener<DecodedFile>() {
            @Override
            public void onResponse(DecodedFile decodedFile) {
                Log.e(TAG, "get File ****  ok");

                Date d2 = new Date(System.currentTimeMillis());
                long d2s = d2.getTime() / 1000;
                Log.e(TAG, "Delta Time get File: " + (d2s - d1s) + "  **********************");

                if (decodedFile != null && decodedFile.fileNameAndExtension != null && !decodedFile.fileNameAndExtension.trim().isEmpty()) {

                    AttachFile attachFile = new AttachFile();
                    attachFile.id = resourceId;
                    attachFile.subDir = subDir;
                    attachFile.filePath = "";
                    attachFile.fileNameAndExtension = decodedFile.fileNameAndExtension;

                    Log.e(TAG, "SUBDIR FILE: " + attachFile.subDir);
                    listener.onResponse(attachFile);
                } else {
                    listener.onResponse(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "get File Failed...");

                Date d2 = new Date(System.currentTimeMillis());
                long d2s = d2.getTime() / 1000;
                Log.e(TAG, "Delta Time Error get File: " + (d2s - d1s) + "  **********************");

                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request, true, FILE_TIMEOUT_MS);
    }
    */

    // TODO new metho to avod get B64 data, only name and extension file
    public void getFile(final Context ctx, final String subDir, final long resourceId, final Response.Listener<AttachFile> listener, final Response.ErrorListener errorListener) {

        Log.e(TAG, "GET FILE BY ID!!!: " + resourceId);

        final Date d1 = new Date(System.currentTimeMillis());
        final long d1s = d1.getTime() / 1000;

        HRequest request = RestApiServices.createGetFileImageRequest(resourceId, new Response.Listener<DecodedFile>() {
            @Override
            public void onResponse(DecodedFile decodedFile) {
                Log.e(TAG, "get File ****  ok");

                Date d2 = new Date(System.currentTimeMillis());
                long d2s = d2.getTime() / 1000;
                Log.e(TAG, "Delta Time get File: " + (d2s - d1s) + "  **********************");

                if (decodedFile != null && decodedFile.fileNameAndExtension != null && !decodedFile.fileNameAndExtension.trim().isEmpty()) {

                    AttachFile attachFile = new AttachFile();
                    attachFile.id = resourceId;
                    attachFile.subDir = subDir;
                    attachFile.filePath = "";
                    attachFile.fileNameAndExtension = decodedFile.fileNameAndExtension;

                    Log.e(TAG, "SUBDIR FILE: " + attachFile.subDir);
                    listener.onResponse(attachFile);
                } else {
                    listener.onResponse(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "get File Failed...");

                Date d2 = new Date(System.currentTimeMillis());
                long d2s = d2.getTime() / 1000;
                Log.e(TAG, "Delta Time Error get File: " + (d2s - d1s) + "  **********************");

                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request, true, FILE_TIMEOUT_MS);
    }


    public void getDecodedFile(final Context ctx, final AttachFile attachFile, final Response.Listener<File> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createGetFileRequest(attachFile.id, new Response.Listener<DecodedFile>() {
            @Override
            public void onResponse(DecodedFile decodedFile) {
                Log.e(TAG, "get DECODED File ****  ok");

                if (decodedFile.fileNameAndExtension != null && !decodedFile.fileNameAndExtension.trim().isEmpty()) {

                    File file = FileHelper.decodeAttachFile(ctx, attachFile.subDir, decodedFile.fileNameAndExtension, decodedFile.fileImage);
                    if (file != null) {
                        Log.e(TAG, "FILE PATH!!!!!! : " + file.getPath());
                        listener.onResponse(file);
                    } else {
                        listener.onResponse(null);
                    }

                } else {
                    listener.onResponse(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "get File Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, true);
    }

    public void removeFile(long attachId, final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createRemoveFileRequest(attachId, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void result) {
                Log.e(TAG, "Remove File  ok");
                listener.onResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Remove File Error...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request, false, FILE_TIMEOUT_MS);
    }


    public void attachFile(long desId, long attachId, final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createAttachFileRequest(desId, attachId, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void result) {
                Log.e(TAG, "Attach File  ok");
                listener.onResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Attach File Error...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request, false, FILE_TIMEOUT_MS);
    }

    public void removeAttachFile(long attachId, final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createRemoveAttachFileRequest(attachId, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void result) {
                Log.e(TAG, "Remove Attch File  ok");
                listener.onResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Remove Attach File Error...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request, false, FILE_TIMEOUT_MS);
    }


    public void cancelRequest() {
        AppController.getInstance().getRestEngine().cancelPendingRequests();
    }


    public void getAffiliationInfo(final long affiliationCardId, final Response.Listener<AffiliationCardInfo> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createGetCardInfoRequest(affiliationCardId, new Response.Listener<AffiliationCardInfo>() {
            @Override
            public void onResponse(AffiliationCardInfo affiliationCardInfo) {
                Log.e(TAG, "get AffiliationCardInfo  ok");
                listener.onResponse(affiliationCardInfo);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "get AffiliationCardInfo Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }

    public void getEmpresaLaboralListData(final long affiliationCardId, final Response.Listener<List<EntidadEmpleadora>> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createGetEmpresaLaboralList(affiliationCardId, new Response.Listener<List<EntidadEmpleadora>>() {
            @Override
            public void onResponse(List<EntidadEmpleadora> resultList) {
                Log.e(TAG, "get Empresa LAboral Data  ok");
                listener.onResponse(resultList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "get Empresa LAboral Data Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }


    public void getAffiliationData(final long affiliationCardId, final Response.Listener<AffiliationCard> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createGetCardRequest(affiliationCardId, new Response.Listener<AffiliationCard>() {
            @Override
            public void onResponse(AffiliationCard affiliationCard) {
                Log.e(TAG, "get AffiliationCard  ok");
                listener.onResponse(affiliationCard);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "get AffiliationCard Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }


    public void updateAffilliationData(final AffiliationCard affiliationCard, final Response.Listener<AffiliationDataResult> listener, final Response.ErrorListener errorListener) {


        HRequest request = null;
        if (affiliationCard.id == -1L) {
            request = RestApiServices.createCardRequest(affiliationCard, new Response.Listener<AffiliationDataResult>() {
                @Override
                public void onResponse(AffiliationDataResult affiliationResponse) {
                    Log.e(TAG, "create affilliation  Successs");

                    // save actual cardId;
                    Storage.getInstance().setAffiliationCardId(affiliationResponse.cardId);
                    listener.onResponse(affiliationResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(TAG, "create affilliation Failed...");
                    errorListener.onErrorResponse(volleyError);
                }
            });

        } else {
            request = RestApiServices.updateCardRequest(affiliationCard, new Response.Listener<AffiliationDataResult>() {
                @Override
                public void onResponse(AffiliationDataResult affiliationResponse) {
                    Log.e(TAG, "update affilliation  Successs");

                    // save actual cardId;
                    Storage.getInstance().setAffiliationCardId(affiliationResponse.cardId);
                    listener.onResponse(affiliationResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(TAG, "update affilliation Failed...");
                    errorListener.onErrorResponse(volleyError);
                }
            });
        }

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }

    public void verifyTicketPagoAnticipado(final long affiliationCardId, final Response.Listener<Boolean> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.verifyTicketPagoAnticipadoRequest(affiliationCardId, new Response.Listener<Boolean>() {
            @Override
            public void onResponse(Boolean result) {
                Log.e(TAG, "verifyTicketPagoAnticipado affilliation  Successs");
                listener.onResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "update affilliation Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }


    public void updateFormaPago(final Pago pago, final Response.Listener<Pago> listener, final Response.ErrorListener errorListener) {
        HRequest request = null;
/*
        if (pago.id == -1) {
            // POST
            request = RestApiServices.createCardFormaPagoRequest(pago, new Response.Listener<Pago>() {
                @Override
                public void onResponse(Pago resultPago) {
                    Log.e(TAG, "create afilliation  forma pago Successs");
                    if (resultPago != null) {
                        pago.id = resultPago.id;
                    }
                    listener.onResponse(resultPago);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(TAG, "create afilliation  forma pago  Failed...");
                    errorListener.onErrorResponse(volleyError);
                }
            });
        } else {
            // PUT
            request = RestApiServices.updateCardFormaPagoRequest(pago, new Response.Listener<Pago>() {
                @Override
                public void onResponse(Pago response) {
                    Log.e(TAG, "update afilliation  forma pago Successs");
                    listener.onResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(TAG, "update afilliation  forma pago  Failed...");
                    errorListener.onErrorResponse(volleyError);
                }
            });
        }
*/ //TODO Forma de Pago con Copagos
        if (pago.hasCopagos){
            if (pago.id == -1) {
                // POST
                request = RestApiServices.createCardFormaPagoConCopagoRequest(pago, new Response.Listener<Pago>() {
                    @Override
                    public void onResponse(Pago resultPago) {
                        Log.e(TAG, "create afilliation  forma pago Successs");
                        if (resultPago != null) {
                            pago.id = resultPago.id;
                        }
                        listener.onResponse(resultPago);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e(TAG, "create afilliation  forma pago  Failed...");
                        errorListener.onErrorResponse(volleyError);
                    }
                });
            } else {
                // PUT
                request = RestApiServices.updateCardFormaPagoConCopagoRequest(pago, new Response.Listener<Pago>() {
                    @Override
                    public void onResponse(Pago response) {
                        Log.e(TAG, "update afilliation  forma pago Successs");
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e(TAG, "update afilliation  forma pago  Failed...");
                        errorListener.onErrorResponse(volleyError);
                    }
                });
            }
        } else {
            if (pago.id == -1) {
                // POST
                request = RestApiServices.createCardFormaPagoRequest(pago, new Response.Listener<Pago>() {
                    @Override
                    public void onResponse(Pago resultPago) {
                        Log.e(TAG, "create afilliation  forma pago Successs");
                        if (resultPago != null) {
                            pago.id = resultPago.id;
                        }
                        listener.onResponse(resultPago);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e(TAG, "create afilliation  forma pago  Failed...");
                        errorListener.onErrorResponse(volleyError);
                    }
                });
            } else {
                // PUT
                request = RestApiServices.updateCardFormaPagoRequest(pago, new Response.Listener<Pago>() {
                    @Override
                    public void onResponse(Pago response) {
                        Log.e(TAG, "update afilliation  forma pago Successs");
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e(TAG, "update afilliation  forma pago  Failed...");
                        errorListener.onErrorResponse(volleyError);
                    }
                });
            }
        }
        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }


    public void addEntidadEmpleadora(final EntidadEmpleadora ee, final Response.Listener<Long> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createCardEntidadEmpleadoraRequest(ee, new Response.Listener<Long>() {
            @Override
            public void onResponse(Long empId) {
                Log.e(TAG, "create EntidadEmpleadora Successs");
                listener.onResponse(empId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "create EntidadEmpleadora Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }


    public void updateObraSocialData(final ObraSocial os, final Response.Listener<Long> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createCardOSRequest(os, new Response.Listener<Long>() {
            @Override
            public void onResponse(Long osId) {
                Log.e(TAG, "create Obra social Successs");
                listener.onResponse(osId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "create Obra Social Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }


    public void verifyCreditCard(long cardId, String fechaCardVencimiento, String fechaIniServ, final Response.Listener<Boolean> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.verifyCreditCardRequest(cardId, fechaCardVencimiento, fechaIniServ, new Response.Listener<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                Log.e(TAG, "validateCreditCardRequest Succcesss");
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "validateCreditCardReques Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }


    // LINKS
    public void getLinkRequest(String linkId, final Response.Listener<LinkData> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.linkRequest(linkId, new Response.Listener<LinkData>() {
            @Override
            public void onResponse(LinkData resultLink) {
                Log.e(TAG, "get Link  Data  ok");
                listener.onResponse(resultLink);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "getLink Data Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }

}
