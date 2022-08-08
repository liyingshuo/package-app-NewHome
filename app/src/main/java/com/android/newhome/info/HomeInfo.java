package com.android.newhome.info;

import com.android.newhome.internet.typedefined.TypeDefined;

public class HomeInfo {
    int m_type;

    // total 10 attribute
    int m_id; //unique number from mysql

    String m_address; //beijing-haidian-qinghe-abcxxxx
    String m_detailAddress; //3-6-503
    String m_hight; //9/23
    String m_describe;

    int m_price; //5000
    int m_environment; // 012345 5 is best
    int m_misInformation; // misinformation count

    boolean m_elevator; //true

    // picture file
    String m_filePath; //home/lys/package-app-NewHomeInfo/HomeInfo/m_id/


    public HomeInfo() {
        m_type = TypeDefined.TYPE_REQUEST_HOME;
        m_id = 0;
        m_address = "";
        m_detailAddress = "";
        m_hight = "";
        m_describe = "";
        m_price = 0;
        m_environment = 0;
        m_misInformation = 0;
        m_elevator = false;
        m_filePath = "home/lys/package-app-NewHomeInfo/HomeInfo/" + m_id + "/";
    }


    public HomeInfo(String m_address, String m_detailAddress, String m_hight, String m_describe, int m_price, int m_environment, boolean m_elevator) {
        this.m_type = TypeDefined.TYPE_REQUEST_HOME;
        this.m_id = 0;
        this.m_address = m_address;
        this.m_detailAddress = m_detailAddress;
        this.m_hight = m_hight;
        this.m_describe = m_describe;
        this.m_price = m_price;
        this.m_environment = m_environment;
        this.m_misInformation = 0;
        this.m_elevator = m_elevator;
        this.m_filePath = "home/lys/package-app-NewHomeInfo/HomeInfo/" + m_id + "/";
    }
}
