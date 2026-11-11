package com.example.stokbarang;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context c){super(c,"stok_barang.db",null,1);}
    public void onCreate(SQLiteDatabase d){
        d.execSQL("CREATE TABLE materials(id INTEGER PRIMARY KEY AUTOINCREMENT,code TEXT UNIQUE,name TEXT NOT NULL,category TEXT,unit TEXT,stock REAL DEFAULT 0,min_stock REAL DEFAULT 0,active INTEGER DEFAULT 1)");
        d.execSQL("CREATE TABLE transactions(id INTEGER PRIMARY KEY AUTOINCREMENT,material_id INTEGER,type TEXT,quantity REAL,date TEXT,note TEXT,stock_before REAL,stock_after REAL)");
        seed(d);
    }
    public void onUpgrade(SQLiteDatabase d,int o,int n){}
    void seed(SQLiteDatabase d){
        String[] groups={"Besi beton","Besi ulir","Wiremesh","Semen","Pasir","Batu split","Bata","Batako","Bata ringan","Paving block","Pipa PVC","Pipa PPR","Pipa galvanis","Kabel NYA","Kabel NYM","MCB","Saklar","Stop kontak","Cat tembok","Cat besi","Plamir","Keramik","Granite tile","Kloset","Kran air","Kayu kaso","Papan kayu","Multipleks","Paku","Sekrup","Hollow galvanis","Baja ringan","Plat besi","Besi WF","Elektroda las","Ready mix","U-ditch","Box culvert","Geotextile","Pipa HDPE"};
        String[] sizes={"6 mm","8 mm","10 mm","12 mm","13 mm","16 mm","19 mm","22 mm","25 mm","32 mm"};
        String[] units={"btg","btg","lembar","zak","m3","m3","pcs","pcs","pcs","m2","btg","btg","btg","roll","roll","pcs","pcs","pcs","pail","pail","pail","dus","dus","pcs","pcs","btg","lembar","lembar","kg","dus","btg","btg","lembar","btg","kg","m3","pcs","pcs","m2","btg"};
        int k=0;
        for(int g=0;g<groups.length;g++) for(int j=0;j<5;j++){
            ContentValues v=new ContentValues();
            v.put("code",String.format(java.util.Locale.US,"MAT-%03d",++k));
            v.put("name",groups[g]+" "+sizes[j]); v.put("category",groups[g]);
            v.put("unit",units[g]); v.put("stock",0);v.put("min_stock",0);v.put("active",1);
            d.insertWithOnConflict("materials",null,v,SQLiteDatabase.CONFLICT_IGNORE);
        }
    }
    Cursor materials(String q){return getReadableDatabase().rawQuery("SELECT id,code,name,category,unit,stock,min_stock FROM materials WHERE active=1 AND (name LIKE ? OR code LIKE ?) ORDER BY name",new String[]{"%"+q+"%","%"+q+"%"});}
    long save(long id,String code,String name,String cat,String unit,double min){
        ContentValues v=new ContentValues();v.put("code",code);v.put("name",name);v.put("category",cat);v.put("unit",unit);v.put("min_stock",min);
        if(id==0)return getWritableDatabase().insert("materials",null,v);
        return getWritableDatabase().update("materials",v,"id=?",new String[]{""+id});
    }
    boolean deactivate(long id){ContentValues v=new ContentValues();v.put("active",0);return getWritableDatabase().update("materials",v,"id=?",new String[]{""+id})>0;}
    boolean transact(long id,String type,double qty,String date,String note){
        if(qty<=0)return false; SQLiteDatabase d=getWritableDatabase();d.beginTransaction();
        try{
            Cursor c=d.rawQuery("SELECT stock FROM materials WHERE id=?",new String[]{""+id});
            if(!c.moveToFirst()){c.close();return false;} double before=c.getDouble(0);c.close();
            double after=type.equals("MASUK")?before+qty:before-qty;if(after<0)return false;
            ContentValues s=new ContentValues();s.put("stock",after);d.update("materials",s,"id=?",new String[]{""+id});
            ContentValues t=new ContentValues();t.put("material_id",id);t.put("type",type);t.put("quantity",qty);t.put("date",date);t.put("note",note);t.put("stock_before",before);t.put("stock_after",after);d.insert("transactions",null,t);
            d.setTransactionSuccessful();return true;
        }finally{d.endTransaction();}
    }
    Cursor history(){return getReadableDatabase().rawQuery("SELECT t.date,m.name,t.type,t.quantity,m.unit,t.stock_before,t.stock_after,t.note FROM transactions t JOIN materials m ON m.id=t.material_id ORDER BY t.id DESC",null);}
    Cursor summary(){return getReadableDatabase().rawQuery("SELECT COUNT(*),SUM(CASE WHEN stock<=min_stock THEN 1 ELSE 0 END) FROM materials WHERE active=1",null);}
}
