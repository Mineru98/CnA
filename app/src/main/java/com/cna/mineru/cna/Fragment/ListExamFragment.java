package com.cna.mineru.cna.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cna.mineru.cna.Adapter.RcExamAdapter;
import com.cna.mineru.cna.DB.ExamSQLClass;
import com.cna.mineru.cna.DTO.ExamData;
import com.cna.mineru.cna.R;

import java.util.ArrayList;

public class ListExamFragment extends Fragment {

    public RcExamAdapter mAdapater;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<ExamData> list  = new ArrayList<ExamData>();
    private ExamSQLClass db;
    private View view;
    public RecyclerView rv;


    public ListExamFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_exam, container, false);
        rv = (RecyclerView)view.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        rv.addItemDecoration(new DividerItemDecoration(getActivity(),linearLayoutManager.getOrientation()));
        rv.setLayoutManager(linearLayoutManager);

        db = new ExamSQLClass(getActivity());
        list = db.load_values();

        mAdapater = new RcExamAdapter(getContext(), R.layout.item, list);
        rv.setAdapter(mAdapater);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        list = db.load_values();
        mAdapater = new RcExamAdapter(getContext(), R.layout.item, list);
        rv.setAdapter(mAdapater);
    }
}
