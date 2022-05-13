package com.android.onlinemovieticket.z_smallactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.LoginActivity;
import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.fragment.Lay_bottom;
import com.android.onlinemovieticket.repository.AdminRepository;
import com.android.onlinemovieticket.repository.HallRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class List_Hall extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titlename;
    private ImageButton addHall;

    private EditText searchEdit;
    private Button searchButton;

    private ProgressBar progressBar;

    private List<Hall> hallList = new ArrayList<>();
    private ListView hallView;
    private MyAdapter myAdapter;

    private String account;
    private String type;
    private int cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_hall);

        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titlename = (TextView) findViewById(R.id.title_name);
        titlename.setText("影厅列表");
        addHall = (ImageButton) findViewById(R.id.title_button_add);
        addHall.setVisibility(View.VISIBLE);
        addHall.setOnClickListener(this);

        searchEdit = (EditText) findViewById(R.id.list_hall_searchEdit);
        searchButton = (Button) findViewById(R.id.list_hall_searshButton);
        searchButton.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.list_hall_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        cid = getIntent().getIntExtra("cid", 0);

        hallView = (ListView) findViewById(R.id.list_hall_hallView);
        myAdapter = new MyAdapter();
        hallView.setAdapter(myAdapter);
        initHalls();
        hallView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(List_Hall.this, Update_Hall.class);
                intent.putExtra("hid", hallList.get(position).getHid());
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                Bundle bundle = new Bundle();
                bundle.putSerializable("hall", hallList.get(position));
                bundle.putSerializable("hallList", (Serializable) hallList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        hallView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                delConfirm(hallList.get(position));
                return true;
            }
        });
        showBottom();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                loginConfirm();
                break;
            case R.id.title_button_add:
                addConfirm();
                break;
            case R.id.list_hall_searshButton:
                String search = searchEdit.getText().toString();
                if (search.equals("")) {
                    Toast.makeText(List_Hall.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    searchHall(search);
                }
                break;
            default:
                break;
        }
    }

    private void addConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(List_Hall.this);
        builder.setTitle("提示");
        builder.setMessage("确定要添加放映厅吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(List_Hall.this, Info_Hall.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                intent.putExtra("cid", cid);
                startActivity(intent);
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

    private void delConfirm(final Hall hh) {
        AlertDialog.Builder builder = new AlertDialog.Builder(List_Hall.this);
        builder.setTitle("删除确认");
        builder.setMessage("确定要删除{"+hh.getHname()+"}吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delHall(hh);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void delHall(final Hall hall) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HallRepository hallRepository = new HallRepository();
                boolean flag = hallRepository.delHall(hall);
                if (flag) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(List_Hall.this, "删除成功",
                                    Toast.LENGTH_SHORT).show();
                            initHalls();
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(List_Hall.this, "删除失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 返回登录界面确认
     */
    private void loginConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("返回登录页面？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(List_Hall.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showBottom() {
        FragmentManager manager = getSupportFragmentManager();  //获取FragmentManager
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        bundle.putString("type", type);
        bundle.putInt("cid", cid);
        Fragment bottom_fragment = new Lay_bottom();
        bottom_fragment.setArguments(bundle);
        transaction.add(R.id.list_hall_frame, bottom_fragment).commit();
    }

    private void initHalls() {
        new Thread() {
            @Override
            public void run() {
                int msg = 0;
                HallRepository hallRepository = new HallRepository();
                hallList = hallRepository.findAllHall(cid);
                if (hallList.size() == 0) {
                    msg = 1;
                }

                hand.sendEmptyMessage(msg);

            }
        }.start();
    }

    private void searchHall(String search) {
        List<Hall> tempList = new ArrayList<>();
        for (int i = 0; i < hallList.size(); i++) {
            if (hallList.get(i).getHname().contains(search)) {
                tempList.add(hallList.get(i));
            }
        }
        if (tempList.size() == 0) {
            Toast.makeText(List_Hall.this, "没有搜索到相关信息", Toast.LENGTH_SHORT).show();
        } else {
            hallList.clear();
            hallList.addAll(tempList);
            myAdapter.notifyDataSetChanged();
        }
        progressBar.setVisibility(View.GONE);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return hallList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1;
            if (view == null) {    //打气筒 取得xml 里定义的view
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                view1 = inflater.inflate(R.layout.item_hall, null);
            } else view1 = view;

            TextView name = (TextView) view1.findViewById(R.id.item_hall_name);
            TextView scale = (TextView) view1.findViewById(R.id.item_hall_scale);

            Hall hall = hallList.get(i);

            name.setText(hall.getHname());
            scale.setText(hall.getRow() + "行" + hall.getColumn() + "列");

            return view1;
        }
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 0) {
                myAdapter.notifyDataSetChanged();
            } else if (msg.what == 1) {
                Toast.makeText(List_Hall.this, "没有找到放映厅", Toast.LENGTH_SHORT).show();
            }

        }
    };
}