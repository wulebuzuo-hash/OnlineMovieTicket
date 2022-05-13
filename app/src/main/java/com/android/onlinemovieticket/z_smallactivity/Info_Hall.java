package com.android.onlinemovieticket.z_smallactivity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.db.Uh;
import com.android.onlinemovieticket.repository.HallRepository;
import com.android.onlinemovieticket.repository.UhRepository;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Info_Hall extends AppCompatActivity implements View.OnClickListener {
    private Button navButton;
    private TextView titleName;

    private LinearLayout layout;
    private List<LinearLayout> layoutList = new ArrayList<>();
    private List<EditText> nameEditList = new ArrayList<>();
    private List<EditText> rowEditList = new ArrayList<>();
    private List<EditText> columnEditList = new ArrayList<>();

    private RadioButton addBtn;
    private RadioButton deleteBtn;

    private Button submit;
    private ProgressBar progressBar;

    private String account;
    private String type;
    private int cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_hall);

        layout = findViewById(R.id.info_hall_layout);

        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("放映厅");

        nameEditList.add(findViewById(R.id.info_hall_name1));
        rowEditList.add(findViewById(R.id.info_hall_row1));
        columnEditList.add(findViewById(R.id.info_hall_column1));

        addBtn = (RadioButton) findViewById(R.id.info_hall_add);
        addBtn.setOnClickListener(this);
        deleteBtn = (RadioButton) findViewById(R.id.info_hall_delete);
        deleteBtn.setOnClickListener(this);

        submit = (Button) findViewById(R.id.info_hall_submit);
        submit.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.info_hall_progressbar);
        progressBar.setVisibility(View.GONE);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        cid = getIntent().getIntExtra("cid", 0);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                backConfirm();
                break;
            case R.id.info_hall_add:
                addHall();
                break;
            case R.id.info_hall_delete:
                delHall();
                break;
            case R.id.info_hall_submit:
                progressBar.setVisibility(View.VISIBLE);
                submitHall();
                break;
            default:
                break;
        }
    }

    private void backConfirm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("返回首页？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Info_Hall.this, MovieActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void addHall() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                MATCH_PARENT, WRAP_CONTENT);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                200, WRAP_CONTENT);

        LinearLayout linearLayout1 = new LinearLayout(this);
        linearLayout1.setLayoutParams(params);
        linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
        TextView nameText = new TextView(this);
        nameText.setText("放映厅名：");
        nameText.setTextSize(20);
        EditText nameEdit = new EditText(this);
        nameEdit.setLayoutParams(params);
        nameEditList.add(nameEdit);
        linearLayout1.addView(nameText);
        linearLayout1.addView(nameEdit);

        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setLayoutParams(params);
        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        TextView text = new TextView(this);
        text.setText("影厅规模：");
        text.setTextSize(20);
        EditText rowEdit = new EditText(this);
        rowEdit.setLayoutParams(params2);
        rowEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        rowEditList.add(rowEdit);
        TextView rowText = new TextView(this);
        rowText.setText("行");
        rowText.setTextSize(20);
        EditText columnEdit = new EditText(this);
        columnEdit.setLayoutParams(params2);
        columnEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        columnEditList.add(columnEdit);
        TextView columnText = new TextView(this);
        columnText.setText("列");
        columnText.setTextSize(20);
        linearLayout2.addView(text);
        linearLayout2.addView(rowEdit);
        linearLayout2.addView(rowText);
        linearLayout2.addView(columnEdit);
        linearLayout2.addView(columnText);

        LinearLayout linearLayout3 = new LinearLayout(this);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                MATCH_PARENT, WRAP_CONTENT);
        linearLayout3.setLayoutParams(params3);
        linearLayout3.setOrientation(LinearLayout.VERTICAL);
        linearLayout3.addView(linearLayout1);
        linearLayout3.addView(linearLayout2);

        layout.addView(linearLayout3);
        layoutList.add(linearLayout3);
    }

    private void delHall() {
        if (layoutList.size() > 0) {
            layout.removeView(layoutList.get(layoutList.size() - 1));
            layoutList.remove(layoutList.size() - 1);
            nameEditList.remove(nameEditList.size() - 1);
            rowEditList.remove(rowEditList.size() - 1);
            columnEditList.remove(columnEditList.size() - 1);
        } else {
            Toast.makeText(this, "不能再删除啦", Toast.LENGTH_SHORT).show();
        }
    }


    private void submitHall() {
        List<String> nameList = new ArrayList<>();
        nameList.add(nameEditList.get(0).getText().toString());
        List<String> rowList = new ArrayList<>();
        rowList.add(rowEditList.get(0).getText().toString());
        List<String> columnList = new ArrayList<>();
        columnList.add(columnEditList.get(0).getText().toString());
        if (nameList.get(0).equals("") || rowList.get(0).equals("") || columnList.get(0).equals("")) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
        } else {
            boolean flag = true;
            for (int i = 1; i < nameEditList.size(); i++) {
                String name = nameEditList.get(i).getText().toString();
                String row = rowEditList.get(i).getText().toString();
                String column = columnEditList.get(i).getText().toString();
                if (name.equals("") || row.equals("") || column.equals("")) {
                    Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                    flag = false;
                    break;
                } else if (nameList.contains(name)) {
                    Toast.makeText(this, "影厅名称重复", Toast.LENGTH_SHORT).show();
                } else {
                    nameList.add(name);
                    rowList.add(row);
                    columnList.add(column);
                }
            }
            if (flag) {
                new Thread() {
                    @Override
                    public void run() {
                        HallRepository hallRepository = new HallRepository();
                        UhRepository uhRepository = new UhRepository();
                        int msg = 0;

                        boolean flag = true;
                        int index = 0;

                        for (int i = 0; i < nameList.size(); i++) {
                            Hall hh = hallRepository.findHall(nameList.get(i), cid);
                            if (hh != null) {
                                flag = false;
                                index = i;
                                break;
                            }
                        }

                        if (flag) {
                            for(int i = 0; i < nameList.size(); i++) {
                                String name = nameList.get(i);
                                String row = rowList.get(i);
                                String column = columnList.get(i);
                                boolean flag2 = hallRepository.addHall(new Hall(cid, name,
                                        Integer.parseInt(row), Integer.parseInt(column)));
                                boolean flag3 = uhRepository.addUh(new Uh(
                                        "添加放映厅{" + name + "},规模为"
                                                + row + "行" + column + "列",
                                        hallRepository.findHall(name, cid).getHid(),
                                        "hall", account, getNowDate()));
                                if(flag2 && flag3) {
                                    msg = 1;
                                }
                            }
                        }else {
                            int finalIndex = index;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Info_Hall.this,
                                            "现有放映厅已有{" + nameList.get(finalIndex) +
                                                    "}，添加失败，请修改后重试",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        hand.sendEmptyMessage(msg);
                    }
                }.start();
            }
        }
    }

    //获取现在时间
    private Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 0) {
                Toast.makeText(Info_Hall.this,
                        "添加失败，请检查网络或联系系统管理员！！", Toast.LENGTH_LONG).show();
            } else if (msg.what == 1) {
                Toast.makeText(Info_Hall.this, "添加成功",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Info_Hall.this, List_Hall.class);
                intent.putExtra("cid", cid);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
            }
        }
    };

}