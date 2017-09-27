package br.com.aaascp.gerenciadordepedidos.presentation.ui.main;

import android.os.Bundle;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.repository.dao.OrderDaoMemory;
import butterknife.ButterKnife;

public final class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        OrderDaoMemory.initialize();
    }
}
