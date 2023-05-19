package idv.tfp10105.project_forfun.signin.Guided_Tour;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import idv.tfp10105.project_forfun.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Signin_Guided_Tour_Fragment_Image3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Signin_Guided_Tour_Fragment_Image3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageButton skip;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Signin_Guided_Tour_Fragment_Image3() {
        // Required empty public constructor
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButton();
    }

    private void findViews(View view) {
        skip = view.findViewById(R.id.signin_skip); // 姓名輸入欄位
    }

    private void handleButton() {
        skip.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Signin_Guided_Tour_Fragment_Image3.
     */
    // TODO: Rename and change types and number of parameters
    public static Signin_Guided_Tour_Fragment_Image3 newInstance(String param1, String param2) {
        Signin_Guided_Tour_Fragment_Image3 fragment = new Signin_Guided_Tour_Fragment_Image3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin__guided__tour___image3, container, false);
    }
}