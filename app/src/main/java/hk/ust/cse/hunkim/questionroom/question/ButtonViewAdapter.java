package hk.ust.cse.hunkim.questionroom.question;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import hk.ust.cse.hunkim.questionroom.JoinActivity;
import hk.ust.cse.hunkim.questionroom.R;

/**
 * Created by ReynaldiWijaya on 31/10/15.
 */
public class ButtonViewAdapter extends BaseAdapter{
    private ArrayList<String> rooms = new ArrayList<String>();
    private Context mContext;
    private TextView roomNameView;
    LayoutInflater mLayoutInflater;
    private int marker = 0;


    // Gets the context so it can be used later
    public ButtonViewAdapter(Context c,ArrayList<String> r,TextView roomNameV) {
        mContext = c;
        rooms = r;
        roomNameView = roomNameV;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    // Total number of things contained within the adapter
    public int getCount() {

        return rooms.size();//filenames.length;
    }

    // Require for structure, not really used in my code.
    public Object getItem(int position) {
        return null;
    }

    // Require for structure, not really used in my code. Can
    // be used to get the id of an item in the adapter for
    // manual control.
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position,
                        View convertView, ViewGroup parent) {
        final Button btn;


        final View rootView = mLayoutInflater.inflate(R.layout.joinroomsuggestion, parent,
                false);
        //btn = new Button(mContext);

        if(marker == 0) {
            btn = (Button) rootView.findViewById(R.id.suggestionRoom);
        } else {
            btn = (Button) rootView.findViewById(R.id.suggestionRoom2);

        }



        final String p = rooms.get(position);
        if(p.length() > 10){
            String q = p.substring(0,10) + "...";
            btn.setText(q);
        } else {
            btn.setText(p);
        }

        btn.setFocusable(false);
        btn.setFocusableInTouchMode(false);

        if(marker == 0) {
            btn.setLayoutParams(new GridView.LayoutParams(500, 200));
            btn.setPadding(10, 10, 10, 10);
            marker = 1;
        } else {
            btn.setLayoutParams(new GridView.LayoutParams(500, 200));
            btn.setPadding(10, 10, 10, 10);
            marker = 0;
        }





        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                JoinActivity m = (JoinActivity) v.getContext();

                roomNameView.setText(p);
                m.attemptJoin(roomNameView);
            }
        });
        // filenames is an array of strings
        //btn.setTextColor(Color.WHITE);
        //btn.setBackgroundResource(R.drawable.button);
        //btn.setId(position);

        return btn;
    }
}
