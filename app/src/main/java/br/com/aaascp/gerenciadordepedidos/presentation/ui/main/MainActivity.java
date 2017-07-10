package br.com.aaascp.gerenciadordepedidos.presentation.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;

/**
 * Created by andre on 09/07/17.
 */

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
    }
}
