package com.zff.database;

import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.zff.xpanel.parser.ui.MyApp;
import com.zff.xpanel.parser.view.ViewArgs;

public class LiteOrmSingle {

    private static LiteOrmSingle mInstance;

    private LiteOrm liteOrm;

    private LiteOrmSingle(){
        iniLiteOrm(MyApp.getMyAppContext());
    }

    public static LiteOrmSingle getInstance(){
        if(mInstance == null){
            mInstance = new LiteOrmSingle();
        }
        return mInstance;
    }

    private void iniLiteOrm(Context context){
        DataBaseConfig dbConfig = new DataBaseConfig(context, "xPanelParse.db");
        liteOrm = LiteOrm.newCascadeInstance(dbConfig);
    }

    public void insert(ViewArgs viewArgs){
        liteOrm.insert(viewArgs);
    }
    public void update(ViewArgs viewArgs){
        liteOrm.update(viewArgs);
    }
    public void save(ViewArgs viewArgs){

    }
    public void query(String  tag){

    }
    public void delete(ViewArgs viewArgs){

    }

}
