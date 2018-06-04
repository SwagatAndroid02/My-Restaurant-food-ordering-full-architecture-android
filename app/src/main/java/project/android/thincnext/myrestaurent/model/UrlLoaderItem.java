package project.android.thincnext.myrestaurent.model;

/**
 * Created by thincnext on 18-Jan-18.
 */

public class UrlLoaderItem {

    String _id;
    String urlName;
    String urlAddress;

   /* public UrlLoaderItem(String urlName, String urlAddress){
        this.urlName=urlName;
        this.urlAddress=urlAddress;
    }*/

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public String getUrlAddress() {
        return urlAddress;
    }

    public void setUrlAddress(String urlAddress) {
        this.urlAddress = urlAddress;
    }
}
