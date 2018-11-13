package wanglihua.liveconsole.Handle;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import wanglihua.liveconsole.Model.MiniMasterInfo;
import wanglihua.liveconsole.Utils.Utils;


public class ConfigureHandle extends DefaultHandler {

    public ConfigureHandle(Context context) {
        this.mContext = context;
    }


    class result {
        public int State, XML;
        public String Msg;

        public result() {
            State = -1;
            XML = 0;
            Msg = "";
        }
    }

    private Context mContext;

    private result result = new result();
    private ArrayList<MiniMasterInfo> mList = new ArrayList<MiniMasterInfo>();

    public result getResult() {
        return result;
    }

    public void setResult(result result) {
        this.result = result;
    }

    public void rest() {
        result = new result();
    }

    public ArrayList<MiniMasterInfo> readXML(InputStream inStream) {
        if (inStream == null) {

            return null;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder builder = factory.newDocumentBuilder();

            Document dom = builder.parse(inStream);

            Element root = dom.getDocumentElement();

            NodeList items = root.getElementsByTagName("Table");
            if (items.getLength() == 0) {

            } else {
                Element mediaNode = (Element) items.item(0);

                NodeList childsNodes = mediaNode.getChildNodes();

                for (int j = 0; j < childsNodes.getLength(); j++) {

                    Node node = (Node) childsNodes.item(j);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element childNode = (Element) node;

                        if ("State".equals(childNode.getNodeName())) {
                            result.State = new Integer(childNode.getFirstChild()
                                    .getNodeValue());

                        } else if ("XML".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() == null) {
                                result.XML = 0;
                            } else {
                                result.XML = new Integer(childNode.getFirstChild()
                                        .getNodeValue());

                            }

                        } else if ("Msg".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() == null) {
                                result.Msg = "";
                            } else {
                                if ((result.State == -1) && (result.XML == 0)) {
                                    result.Msg = childNode.getFirstChild()
                                            .getNodeValue();

                                } else if ((result.State == 0) && (result.XML == 1)) {
                                    result.Msg = Utils.DecryptDoNet(childNode.getFirstChild()
                                            .getNodeValue());

                                }

                            }

                        }
                    }

                }

            }
            inStream.close();
        } catch (Exception e) {

            result = new result();

            return null;

        }
//        		Log.e("WLH", ""+result.State);
//        		Log.e("WLH", ""+result.XML);
//        		Log.e("WLH", ""+result.Msg);
        if ((result.State == 0) && (result.XML == 1)) {
            return readMiniMasterInfo();
        } else if ((result.State == -1) && (result.XML == 0)) {
            Utils.AlertDialog("提示ʾ", result.Msg, mContext);
            return null;
        } else {
            Utils.AlertDialog("提示ʾ", "网络异常！", mContext);
            return null;
        }

    }


    private ArrayList<MiniMasterInfo> readMiniMasterInfo() {
        mList = new ArrayList<MiniMasterInfo>();
        InputStream is = new ByteArrayInputStream(result.Msg.getBytes());
        if (is == null) {
            return null;
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder builder = factory.newDocumentBuilder();

            Document dom = builder.parse(is);

            Element root = dom.getDocumentElement();

            NodeList items = root.getElementsByTagName("Table");
            for (int i = 0; i < items.getLength(); i++) {
                MiniMasterInfo mInfo = new MiniMasterInfo();

                Element mediaNode = (Element) items.item(i);

                NodeList childsNodes = mediaNode.getChildNodes();

                for (int j = 0; j < childsNodes.getLength(); j++) {

                    Node node = (Node) childsNodes.item(j);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element childNode = (Element) node;

                        if ("id".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() != null) {
                                mInfo.id = childNode.getFirstChild().getNodeValue();
                            } else {
                                mInfo.id = "";
                            }

                        } else if ("mediaURL".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() != null) {
                                mInfo.mediaURL = childNode.getFirstChild().getNodeValue();
                            } else {
                                mInfo.mediaURL = "";
                            }
                        } else if ("videoURL".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() != null) {
                                mInfo.videoURL = childNode.getFirstChild().getNodeValue();
                            } else {
                                mInfo.videoURL = "";
                            }
                        } else if ("DateAndTime".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() != null) {
                                mInfo.DateAndTime = childNode.getFirstChild().getNodeValue();
                            } else {
                                mInfo.DateAndTime = "";
                            }
                        } else if ("itype".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() != null) {
                                mInfo.itype = childNode.getFirstChild().getNodeValue();
                            } else {
                                mInfo.itype = "";
                            }
                        }
                    }

                }
            //    Log.e("WLH", "mInfo  = "+mInfo.id);

                mList.add(mInfo);
            }
            is.close();

     //       Log.e("WLH", "mList.size()  = "+mList.size());

            return mList;
        } catch (Exception e) {
            Log.e("WLH", "" + e.toString());
            mList = new ArrayList<MiniMasterInfo>();
            return null;
        }

    }

}
