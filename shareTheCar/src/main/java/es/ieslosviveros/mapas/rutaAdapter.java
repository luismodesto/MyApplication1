package es.ieslosviveros.mapas;

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

import es.ieslosviveros.chat.ChatContacto;
import es.ieslosviveros.chat.ChatMessage;
import es.ieslosviveros.www.myapplication.R;

/**
 * Created by papa on 01/05/2016.
 */
public class RutaAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    ArrayList<Ruta> rutaList;

    public RutaAdapter(Activity activity, ArrayList<Ruta> list) {
        rutaList = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return rutaList.size();
    }

    @Override
    public Object getItem(int position) {

        return rutaList.get(position);

    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Ruta ruta = (Ruta) rutaList.get(position);
        View vi = convertView;
        if (convertView == null) vi = inflater.inflate(R.layout.chatbubble, null);

        TextView msg = (TextView) vi.findViewById(R.id.message_text);
        msg.setText(ruta.pos);
        LinearLayout layout = (LinearLayout) vi.findViewById(R.id.bubble_layout);
        LinearLayout parent_layout = (LinearLayout) vi.findViewById(R.id.bubble_layout_parent);

        // if message is mine then align to right
  {
            layout.setBackgroundResource(R.drawable.luis3);
            parent_layout.setGravity(Gravity.RIGHT);
            // msg.setBackgroundColor(Color.GREEN);
        }

        msg.setTextColor(Color.BLACK);
        return vi;
    }

    public void add(Ruta object) {
        rutaList.add(object);
    }

    public void clear() {
        rutaList.clear();
    }

}

