package idv.tfp10105.project_forfun.membercenter.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PersonnalVPAdapter extends FragmentStateAdapter  {
    List<Fragment> listFragment;

    public PersonnalVPAdapter(@NonNull @NotNull Fragment fragment, List<Fragment> listFragment) {
        super(fragment);
        this.listFragment = listFragment;
    }

    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getItemCount() {
        return listFragment.size();
    }
}
