package es.ieslosviveros.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import es.ieslosviveros.www.myapplication.R;

/**
 * Created by papa on 29/03/2016.
 */
public class ContactosAdapter extends BaseAdapter {


    private static LayoutInflater inflater = null;
    ArrayList<ChatContacto> chatContactoList;

    public ContactosAdapter(Activity activity, ArrayList<ChatContacto> list) {
        chatContactoList = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return chatContactoList.size();
    }

    @Override
    public Object getItem(int position) {


        return chatContactoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatContacto contacto = (ChatContacto) chatContactoList.get(position);
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.contactoitem, null);
        TextView nombre = (TextView) vi.findViewById(R.id.member_name);
        TextView estado = (TextView) vi.findViewById(R.id.status);
        TextView sin_leer = (TextView) vi.findViewById(R.id.pendientes);

        //vi.findViewById(R.id.pendientes);
                nombre.setText(contacto.nombre);
        estado.setText("");
        sin_leer.setText(""+contacto.sin_leer);
        if (contacto.sin_leer==0)
            sin_leer.setBackgroundResource(0);
        else
            sin_leer.setBackgroundResource(R.drawable.bage_circle);
        return vi;
    }

    public void add(ChatContacto object) {
        chatContactoList.add(object);
    }

    public void clear(){
        chatContactoList.clear();
    }
}


