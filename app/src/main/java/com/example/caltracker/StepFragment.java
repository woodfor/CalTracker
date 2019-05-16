package com.example.caltracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.caltracker.RestModel.User;
import com.example.caltracker.general.tools;
import com.example.caltracker.Room.DailyInfoDatabase;
import com.example.caltracker.Room.DaliyInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepFragment extends Fragment {
    DailyInfoDatabase db = null;
    View vDisplayUnit;
    List<HashMap<String, String>> stepListArray;
    SimpleAdapter myListAdapter;
    ListView stepList;
    HashMap<String,String> map = new HashMap<String,String>();
    String[] colHEAD = new String[] {"Steps","Time"};
    int[] dataCell = new int[] {R.id.textViewSteps,R.id.textViewTime};
    User user;
    EditText ed;
    private Context mContext;
    int infoID;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.fragment_step, container, false);
        ((HomeActivity) getActivity())
                .setActionBarTitle("Your Step Entry");
        final Button btn_add = vDisplayUnit.findViewById(R.id.button_add);
        user = getArguments().getParcelable("User");
        //init finished
        db = Room.databaseBuilder(mContext,
                DailyInfoDatabase.class, "dailyInfo_database")
                .fallbackToDestructiveMigration()
                .build();
        stepList = vDisplayUnit.findViewById(R.id.listView_step);
        ReadDatabase readDatabase = new ReadDatabase();
        readDatabase.execute();

        //init dialog
        final Dialog dialog = new Dialog(mContext);
        final AlertDialog alertDialog =tools.alertDialog(mContext,"Alert","Sure delete ?");
        dialog.setContentView(R.layout.dialog_step_info);
        final TextView tv = dialog.findViewById(R.id.textView);
        ed = dialog.findViewById(R.id.editText);
        final Button btn_edt = dialog.findViewById(R.id.buttonEdit);
        final Button btn_cancel = dialog.findViewById(R.id.buttonCancel);
        final Button btn_del = dialog.findViewById(R.id.buttonDel);
        final Button btn_ok = dialog.findViewById(R.id.buttonOK);
        final Button btn_cancel2 = dialog.findViewById(R.id.buttonCancel2);

        btn_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setVisibility(View.GONE);
                ed.setVisibility(View.VISIBLE);
                btn_edt.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);
                btn_del.setVisibility(View.GONE);
                btn_ok.setVisibility(View.VISIBLE);
                btn_cancel2.setVisibility(View.VISIBLE);
            }
        });

        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteOne().execute();
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String step;
                if((step = ed.getText().toString())!= null && !step.isEmpty()){
                    new UpdateDatabase().execute(step);
                    dialog.dismiss();
                }
                else
                    ed.setError("Should not be empty");
            }
        });
        btn_cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                refreshFrg();
            }
        });

        stepList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> map = (Map<String,String>) parent.getItemAtPosition(position);
                String step = map.get("Steps");
                String time= map.get("Time");
                infoID = Integer.parseInt(map.get("ID"));
                tv.setText(step+" steps at "+ time);
                dialog.show();

                return true;
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edittext = new EditText(mContext);
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                final AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle("Set steps: ").setView(edittext)
                        .setPositiveButton("Ok",null)
                        .setNegativeButton("No",null)
                        .show();
                Button btn_positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = edittext.getText().toString();
                        if (!(text.isEmpty()||text==null) && tools.isInteger(text))
                        {
                            new InsertDatabase().execute(text);
                            dialog.dismiss();
                        }
                        else
                            edittext.setError("Should not be a number");
                    }
                });
            }
        });


        return  vDisplayUnit;
    }

    private class ReadDatabase extends AsyncTask<Void, Void, List<DaliyInfo>> {
        @Override
        protected List<DaliyInfo> doInBackground(Void... params) {
            List<DaliyInfo> infos = db.InfoDao().findByUID(user.getUid());
            if (!( infos == null||infos.isEmpty()) ){

                return infos;
            }
            else
                return null;
        }
        @Override
        protected void onPostExecute(List<DaliyInfo> infos) {
            if (!( infos == null || infos.isEmpty()) ){
                stepListArray = new ArrayList<HashMap<String, String>>();
                for (DaliyInfo i : infos) {
                    map=new HashMap<String, String>();
                    map.put("Steps",i.getSteps()+"");
                    map.put("Time", i.getTime());
                    map.put("ID",i.getId()+"");
                    stepListArray.add(map);
                }
                myListAdapter = new
                        SimpleAdapter(mContext,stepListArray,R.layout.listview_step,colHEAD,dataCell);
                stepList.setAdapter(myListAdapter);
            }
            else{
                tools.toast_long(mContext,"No step entry today");
            }

        }
    }

    private class InsertDatabase extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... params) {
            try{
                DaliyInfo daliyInfo = new DaliyInfo(user.getUid(),Integer.parseInt(params[0]));
                db.InfoDao().insert(daliyInfo);
                return true;
            }
            catch (Exception e)
            {
                return false;
            }

        }
        @Override
        protected void onPostExecute(Boolean flag) {
            //refresh this frg
            if (flag == true)
            {
                refreshFrg();
            }
            else {
                tools.toast_short(mContext,"Room database error");
            }
        }
    }

    private class UpdateDatabase extends AsyncTask<String, Void, Boolean> {
        @Override protected Boolean doInBackground(String... params) {
            try {
                DaliyInfo daliyInfo = db.InfoDao().findByID(infoID);
                daliyInfo.setSteps(Integer.parseInt(params[0]));
                db.InfoDao().updateInfos(daliyInfo);
                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean flag) {
            if (flag == true)
            {
                refreshFrg();
            }
            else {
                tools.toast_short(mContext,"DB error");
            }
        }
    }
    private class DeleteOne extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            db.InfoDao().deleteOne(infoID);
            return null;
        }
        protected void onPostExecute(Void param) {
           refreshFrg();
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void refreshFrg()
    {
        String tag = this.getClass().getName();
        //remove the last two char
        if (tag.indexOf("$")!=-1)
            tag = tag.substring(0,tag.indexOf("$"));
        Fragment frg = null;
        frg = getFragmentManager().findFragmentByTag(tag);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(frg).attach(frg).commit();
    }


}
