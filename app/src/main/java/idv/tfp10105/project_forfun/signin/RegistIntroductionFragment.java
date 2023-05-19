package idv.tfp10105.project_forfun.signin;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;

import idv.tfp10105.project_forfun.R;

public class RegistIntroductionFragment extends Fragment {
    private Activity activity;
    private ImageButton btTenant,btLandlord;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_regist_introduction, container, false);
        btTenant=view.findViewById(R.id.btTenant);
        btLandlord=view.findViewById(R.id.btLandlord);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle=new Bundle();
        btTenant.setOnClickListener(v->{
            bundle.putString("Apply","Tenant");
            Navigation.findNavController(v)
                    .navigate(R.id.registerFragment,bundle);
        });
        btLandlord.setOnClickListener(v->{
            bundle.putString("Apply","Landlord");
            Navigation.findNavController(v)
                    .navigate(R.id.registerFragment,bundle);
        });

    }
}