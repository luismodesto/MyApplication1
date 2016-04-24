package es.ieslosviveros.noticias;

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

import es.ieslosviveros.chat.ChatMessage;
import es.ieslosviveros.www.myapplication.R;

/**
 * Created by papa on 02/04/2016.
 */
public class NoticiaAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    ArrayList<Noticia> noticiaList;

    public NoticiaAdapter(Activity activity, ArrayList<Noticia> list) {
        noticiaList = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return noticiaList.size();
    }

    @Override
    public Object getItem(int position) {

        return noticiaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Noticia noticia = (Noticia) noticiaList.get(position);
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.noticiaitem, null);

        TextView titulo = (TextView) vi.findViewById(R.id.titulo_noticia);
        TextView fecha = (TextView) vi.findViewById(R.id.fecha_noticia);
        titulo.setText(noticia.titulo);
        fecha.setText(noticia.fecha);

        LinearLayout layout         = (LinearLayout) vi.findViewById(R.id.bubble_layout);
        LinearLayout parent_layout  = (LinearLayout) vi.findViewById(R.id.bubble_layout_parent);

        // if message is mine then align to right
        titulo.setTextColor(Color.BLACK);
        return vi;
    }

    public void add(Noticia object) {
        noticiaList.add(object);
    }

    public void clear(){
        noticiaList.clear();
    }

}
