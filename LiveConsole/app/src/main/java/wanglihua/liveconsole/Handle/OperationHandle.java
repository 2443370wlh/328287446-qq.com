package wanglihua.liveconsole.Handle;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import wanglihua.liveconsole.Model.LoginInfo;


public class OperationHandle extends DefaultHandler {

    public OperationHandle(Context context) {
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
    private LoginInfo loginInfo = new LoginInfo();

    public result getResult() {
        return result;
    }

    public void setResult(result result) {
        this.result = result;
    }

    public void rest() {
        result = new result();
    }


    public String readXML(InputStream inStream) {
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
                                result.Msg = childNode.getFirstChild()
                                        .getNodeValue();


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

        //
        //		Log.e("WLH", ""+result.State);
        //		Log.e("WLH", ""+result.XML);
        //		Log.e("WLH", ""+result.Msg);
//        if ((result.State == 0) && (result.XML == 1)) {
//            return readLoginInfo();
//        } else if ((result.State == -1) && (result.XML == 0)) {
//            Utils.AlertDialog("提示ʾ", result.Msg, mContext);
//            return null;
//        } else {
//            Utils.AlertDialog("提示ʾ", "网络异常！", mContext);
//            return null;
//        }
        return result.Msg;
    }


    public String readMsgXML(InputStream inStream) {
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
                                result.Msg = childNode.getFirstChild()
                                        .getNodeValue();


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

        //
        //		Log.e("WLH", ""+result.State);
        //		Log.e("WLH", ""+result.XML);
        //		Log.e("WLH", ""+result.Msg);
        //        if ((result.State == 0) && (result.XML == 1)) {
        //            return readLoginInfo();
        //        } else if ((result.State == -1) && (result.XML == 0)) {
        //            Utils.AlertDialog("提示ʾ", result.Msg, mContext);
        //            return null;
        //        } else {
        //            Utils.AlertDialog("提示ʾ", "网络异常！", mContext);
        //            return null;
        //        }
        return result.Msg;
    }
}
