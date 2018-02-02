package com.cpm.aintupromoter.xmlHandlers;

import com.cpm.aintupromoter.xmlGetterSetter.FailureGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.LoginGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.NonWorkingReasonGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.POSMDATAGetterSetter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


/**
 * Created by jeevanp on 26-07-2017.
 */

public class XMLHandlers {


    // FAILURE XML HANDLER
    public static FailureGetterSetter failureXMLHandler(XmlPullParser xpp, int eventType) {
        FailureGetterSetter failureGetterSetter = new FailureGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("STATUS")) {
                        failureGetterSetter.setStatus(xpp.nextText());
                    }
                    if (xpp.getName().equals("ERRORMSG")) {
                        failureGetterSetter.setErrorMsg(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }

    // LOGIN XML HANDLER
    public static LoginGetterSetter loginXMLHandler(XmlPullParser xpp, int eventType) {
        LoginGetterSetter lgs = new LoginGetterSetter();
        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("RIGHTNAME")) {
                        lgs.setRIGHTNAME(xpp.nextText());
                    }
                    if (xpp.getName().equals("APP_VERSION")) {
                        lgs.setAPP_VERSION(xpp.nextText());
                    }
                    if (xpp.getName().equals("APP_PATH")) {
                        lgs.setAPP_PATH(xpp.nextText());
                    }
                    if (xpp.getName().equals("CURRENTDATE")) {
                        lgs.setCURRENTDATE(xpp.nextText());
                    }
                    if (xpp.getName().equals("Success")) {
                        lgs.setSuccess(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return lgs;
    }

    // JCP XML HANDLER
    public static JourneyPlanGetterSetter JCPXMLHandler(XmlPullParser xpp, int eventType) {
        JourneyPlanGetterSetter jcpGetterSetter = new JourneyPlanGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("META_DATA")) {
                        jcpGetterSetter.setTable_journey_plan(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORE_CD")) {
                        jcpGetterSetter.setStore_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("EMP_CD")) {
                        jcpGetterSetter.setEmp_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("VISIT_DATE")) {
                        jcpGetterSetter.setVISIT_DATE(xpp.nextText());
                    }
                    if (xpp.getName().equals("STORE_NAME")) {
                        jcpGetterSetter.setStore_name(xpp.nextText());
                    }
                    if (xpp.getName().equals("STORE_ADDRESS")) {
                        jcpGetterSetter.setStore_address(xpp.nextText());
                    }
                    if (xpp.getName().equals("CITY")) {
                        jcpGetterSetter.setCity(xpp.nextText());
                    }
                    if (xpp.getName().equals("UPLOAD_STATUS")) {
                        jcpGetterSetter.setUploadStatus(xpp.nextText());
                    }

                    if (xpp.getName().equals("CHECKOUT_STATUS")) {
                        jcpGetterSetter.setCheckOutStatus(xpp.nextText());
                    }
                    if (xpp.getName().equals("GEOTAG")) {
                        jcpGetterSetter.setGeotag(xpp.nextText());
                    }


                    if (xpp.getName().equals("LATITUDE")) {
                        jcpGetterSetter.setLATITUDE(xpp.nextText());
                    }

                    if (xpp.getName().equals("LOGITUDE")) {
                        jcpGetterSetter.setLOGITUDE(xpp.nextText());
                    }


                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jcpGetterSetter;
    }

    public static POSMDATAGetterSetter mappingpromotXML(XmlPullParser xpp, int eventType) {
        POSMDATAGetterSetter mappingpromo = new POSMDATAGetterSetter();
        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        mappingpromo.setMapping_POSM_table(xpp.nextText());
                    }
                    if (xpp.getName().equals("POSM_CD")) {
                        mappingpromo.setPOSM_CD(xpp.nextText());
                    }

                    if (xpp.getName().equals("POSM")) {
                        mappingpromo.setPOSM(xpp.nextText());
                    }
                    if (xpp.getName().equals("FLAG_MANDATORY")) {
                        mappingpromo.setFLAG(xpp.nextText());
                    }
                    if (xpp.getName().equals("SEQUENCE")) {
                        mappingpromo.setSEQUENCE(xpp.nextText());
                    }


                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mappingpromo;
    }

    public static NonWorkingReasonGetterSetter nonWorkinReasonXML(XmlPullParser xpp,
                                                                  int eventType) {
        NonWorkingReasonGetterSetter nonworking = new NonWorkingReasonGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        nonworking.setNonworking_table(xpp.nextText());
                    }
                    if (xpp.getName().equals("REASON_CD")) {
                        nonworking.setReason_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("REASON")) {
                        nonworking.setReason(xpp.nextText());
                    }
                    if (xpp.getName().equals("ENTRY_ALLOW")) {
                        nonworking.setEntry_allow(xpp.nextText());
                    }

                    if (xpp.getName().equals("IMAGE_ALLOW")) {
                        nonworking.setIMAGE_ALLOW(xpp.nextText());
                    }




                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nonworking;
    }



}
