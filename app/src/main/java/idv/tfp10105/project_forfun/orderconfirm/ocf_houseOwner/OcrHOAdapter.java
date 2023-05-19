package idv.tfp10105.project_forfun.orderconfirm.ocf_houseOwner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class OcrHOAdapter extends FragmentStateAdapter {
    public OcrHOAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new OcrHO_reserve();
            case 1:
                return new OcrHO_order();
            case 2:
                return new OcrHO_sign();
            case 3:
                return new OcrHO_pay();
            case 4:
                return new OcrHO_complete();
            case 5:
                return new OcrHO_cancel();
            default:
                return new OcrHO_payarrive();
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }
}
