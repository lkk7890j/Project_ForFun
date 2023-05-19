package idv.tfp10105.project_forfun.orderconfirm.ocf;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OcrAdapter extends FragmentStateAdapter {


    public OcrAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new Ocr_reserve();
            case 1:
                return new Ocr_order();
            case 2:
                return new Ocr_sign();
            case 3:
                return new Ocr_pay();
            case 4:
                return new Ocr_complete();
            case 5:
                return new Ocr_cancel();
            default:
                return new Ocr_paid();
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }
}
