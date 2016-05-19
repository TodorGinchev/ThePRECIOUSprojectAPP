package ui.precious.comnet.aalto.precious;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

import aalto.comnet.thepreciousproject.R;

public class TimelineAdaptor extends BaseAdapter {
    private final Activity actividad;
    private final Vector<String> lista;
    private final Vector<Integer> dibujos;

    public TimelineAdaptor(Activity actividad, Vector<String> lista, Vector<Integer> dibujos) {
        super();
        this.actividad = actividad;
        this.lista = lista;
        this.dibujos = dibujos;
    }

    public View getView(int position, View convertView,
                        ViewGroup parent) {
        LayoutInflater inflater = actividad.getLayoutInflater();
        View view = inflater.inflate(R.layout.ui_timeline_element, null, true);
        TextView textView =(TextView)view.findViewById(R.id.titulo);
        textView.setText(lista.elementAt(position));
        ImageView imageView=(ImageView)view.findViewById(R.id.icono);
        //    try{
        switch (dibujos.get(position)){
//            case 1:
//                imageView.setImageResource(R.drawable.sleeping);
//                break;
//            case 2:
//                imageView.setImageResource(R.drawable.walking);
//                break;
//            case 3:
//                imageView.setImageResource(R.drawable.bicycle);
//                break;
//            case 4:
//                imageView.setImageResource(R.drawable.vehicle);
//                break;
//            case 5:
//                imageView.setImageResource(R.drawable.running);
//                break;
            default:
                imageView.setImageResource(R.drawable.run);
                break;
        }
//          }catch (Exception e){
//        	  Log.e("TimelineAdaptor", "Error accediendo a posicion de vector",e);
//          }
        return view;
    }

    public int getCount() {
        return lista.size();
    }

    public Object getItem(int arg0) {
        return lista.elementAt(arg0);
    }

    public long getItemId(int position) {
        return position;
    }
}