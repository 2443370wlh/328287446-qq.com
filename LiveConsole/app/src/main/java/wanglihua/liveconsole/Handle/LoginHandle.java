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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import wanglihua.liveconsole.Model.LoginInfo;
import wanglihua.liveconsole.Utils.Utils;


public class LoginHandle extends DefaultHandler {

    public LoginHandle(Context context) {
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

    public LoginInfo readXML(InputStream inStream) {
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

        //
        //		Log.e("WLH", ""+result.State);
        //		Log.e("WLH", ""+result.XML);
        //		Log.e("WLH", ""+result.Msg);
        if ((result.State == 0) && (result.XML == 1)) {
            return readLoginInfo();
        } else if ((result.State == -1) && (result.XML == 0)) {
            Utils.AlertDialog("提示ʾ", result.Msg, mContext);
            return null;
        } else {
            Utils.AlertDialog("提示ʾ", "网络异常！", mContext);
            return null;
        }

    }


    private LoginInfo readLoginInfo() {

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
            if (items.getLength() == 0) {

            } else {
                Element mediaNode = (Element) items.item(0);

                NodeList childsNodes = mediaNode.getChildNodes();

                for (int j = 0; j < childsNodes.getLength(); j++) {

                    Node node = (Node) childsNodes.item(j);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element childNode = (Element) node;

                        if ("userID".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() != null) {
                                loginInfo.userID = childNode.getFirstChild().getNodeValue();
                            } else {
                                loginInfo.userID = "";
                            }

                        } else if ("anchor_id".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() != null) {
                                loginInfo.anchor_id = childNode.getFirstChild().getNodeValue();
                            } else {
                                loginInfo.anchor_id = "";
                            }
                        } else if ("sex".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() != null) {
                                loginInfo.sex = childNode.getFirstChild().getNodeValue();
                            } else {
                                loginInfo.sex = "";
                            }
                        } else if ("login_hash".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() != null) {
                                loginInfo.login_hash = childNode.getFirstChild().getNodeValue();
                            } else {
                                loginInfo.login_hash = "";
                            }
                        } else if ("realname".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() != null) {
                                loginInfo.realname = childNode.getFirstChild().getNodeValue();
                            } else {
                                loginInfo.realname = "";
                            }
                        } else if ("picString".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() != null) {
                                loginInfo.picString = childNode.getFirstChild().getNodeValue();
                            } else {
                                loginInfo.picString = "";
                            }
                        } else if ("NickName".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() != null) {
                                loginInfo.group_name = childNode.getFirstChild().getNodeValue();
                            } else {
                                loginInfo.group_name = "";
                            }
                        } else if ("department_name".equals(childNode.getNodeName())) {

                            if (childNode.getFirstChild() != null) {
                                loginInfo.department_name = childNode.getFirstChild().getNodeValue();
                            } else {
                                loginInfo.department_name = "";
                            }
                        }
                    }

                }

                is.close();

            }

            //			Log.e("WLH", ""+loginInfo.login_hash);
            return loginInfo;
        } catch (Exception e) {
            Log.e("WLH", "" + e.toString());
            loginInfo = new LoginInfo();
            return null;
        }

    }

    public String readOUTXML(InputStream inStream) {
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
