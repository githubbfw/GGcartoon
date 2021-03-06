package ggcartoon.yztc.com.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import ggcartoon.yztc.com.Bean.Person;
import ggcartoon.yztc.com.Bean.ShouCang;
import ggcartoon.yztc.com.ggcartoon.LoginAcitivyt;
import ggcartoon.yztc.com.ggcartoon.MainActivity;
import ggcartoon.yztc.com.ggcartoon.ManHuaXiangQingActivity;
import ggcartoon.yztc.com.ggcartoon.R;
import ggcartoon.yztc.com.initerface.Initerface;

/**
 * 书架
 * A simple {@link Fragment} subclass.
 */
public class ShujiaFragment extends Fragment implements Initerface, View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    //布局控件
    @Bind(R.id.lishi)
    TextView lishi;
    @Bind(R.id.shoucang)
    TextView shoucang;
    @Bind(R.id.weishoucang)
    TextView weishoucang;
    @Bind(R.id.shoucang_list)
    ListView shoucangList;
    @Bind(R.id.icon_img)
    ImageView iconImg;
    @Bind(R.id.login)
    TextView login;
    //listview
    private List<ShouCang> list;
    private ShouCangListAdapter shouCangListAdapter;
    private BmobUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shujia, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        initview();
        initdata();
        initviewoper();
    }

    @Override
    public void onResume() {
        super.onResume();
        initview();
        initdata();
        initviewoper();
    }

    //初始化控件
    @Override
    public void initview() {
        user = BmobUser.getCurrentUser();
        if (user != null) {
            login.setText(user.getUsername());
        } else {
            Toast.makeText(getActivity(), "未登陆", Toast.LENGTH_SHORT).show();
        }
        iconImg = (ImageView) getActivity().findViewById(R.id.icon_img);
        shoucang = (TextView) getActivity().findViewById(R.id.shoucang);
        lishi = (TextView) getActivity().findViewById(R.id.lishi);
        weishoucang = (TextView) getActivity().findViewById(R.id.weishoucang);
        shoucangList = (ListView) getActivity().findViewById(R.id.shoucang_list);
        shoucangList.setOnItemClickListener(this);
        shoucangList.setOnItemLongClickListener(this);
        iconImg.setOnClickListener(this);
    }

    @Override
    public void initdata() {
        try {
            //获取数据库中收藏的数据，然后判断是否为空，不为空则设置adapter
            list = MainActivity.dbUtils.findAll(ShouCang.class);
            if (list != null) {
                shoucangList.setVisibility(View.VISIBLE);
                shouCangListAdapter = new ShouCangListAdapter();
                shouCangListAdapter.notifyDataSetChanged();
                shoucangList.setAdapter(shouCangListAdapter);
                weishoucang.setVisibility(View.INVISIBLE);
                for (int i=0;i<list.size();i++){
                    Person p=new Person();
                    p.setUsername(user.getUsername());
                    p.setTitle(list.get(i).getTitle());
                    p.setComicId(list.get(i).getComicId());
                    p.setLastCharpterTitle(list.get(i).getLastCharpterTitle());
                    p.setThumb(list.get(i).getThumb());
                    p.setUpdateTime(list.get(i).getUpdateTime());
                    p.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Log.i("TAG","创建数据成功：" + s);
                            }else{
                                Log.i("TAG","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });
                }

            } else {
                shoucangList.setVisibility(View.INVISIBLE);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initviewoper() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_img:
                Intent intent = new Intent(getActivity(), LoginAcitivyt.class);
                startActivity(intent);
                break;
            case R.id.shoucang:
                //listview显示收藏的漫画
                break;
            case R.id.lishi:
                //listview显示历史记录
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("确认删除收藏？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    MainActivity.dbUtils.delete(ShouCang.class, WhereBuilder.b("title", "=", list.get(position).getTitle()));

                } catch (DbException e) {
                    e.printStackTrace();
                }
                list.remove(position);
                Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                onStart();
                if (list.size() == 0) {
                    shoucangList.setVisibility(View.INVISIBLE);
                    weishoucang.setVisibility(View.VISIBLE);
                    shouCangListAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ManHuaXiangQingActivity.class);
        intent.putExtra("comicId", list.get(position).getComicId());
        startActivity(intent);
    }


    class ShouCangListAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return list != null ? list.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoucang_list_item, null);
                holder.ivThumb = (ImageView) convertView.findViewById(R.id.iv_shoucang_thumb);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_shoucang_title);
                holder.tvUpdateTime = (TextView) convertView.findViewById(R.id.tv_updatetime);
                holder.tvLastCharpterTitle = (TextView) convertView.findViewById(R.id.tv_lastCharpterTitle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ShouCang shoucang = list.get(position);
            holder.tvTitle.setText(shoucang.getTitle());
            holder.tvUpdateTime.setText("更新时间：" + shoucang.getUpdateTime());
            holder.tvLastCharpterTitle.setText("更新到：" + shoucang.getLastCharpterTitle());

            Picasso.with(parent.getContext()).load(shoucang.getThumb()).into(holder.ivThumb);
            return convertView;
        }

        class ViewHolder {
            ImageView ivThumb;
            TextView tvTitle, tvUpdateTime, tvLastCharpterTitle;
        }
    }
}

