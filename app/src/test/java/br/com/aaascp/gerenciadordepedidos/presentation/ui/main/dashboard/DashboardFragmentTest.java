package br.com.aaascp.gerenciadordepedidos.presentation.ui.main.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import br.com.aaascp.gerenciadordepedidos.BuildConfig;
import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list.OrdersListActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.util.DialogUtils;
import br.com.aaascp.gerenciadordepedidos.repository.filter.OrderFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filter.StatusFilter;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.util.FragmentTestUtil.startFragment;

/**
 * Created by andre on 04/10/17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DashboardFragmentTest {

    @Mock
    private DialogUtils dialogUtils;

    @Mock
    private DialogUtils.IntValuesListener listener;

    @Mock
    private DashboardContract.Presenter presenter;

    private DashboardFragment fragment;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        fragment = new DashboardFragment();
        startFragment(fragment);

        fragment.setPresenter(presenter);
    }

    @Test
    public void start_startPresenter() {
        fragment.onStart();
        verify(presenter).start();
    }

    @Test
    public void setToProcessCount() {
        String toProcess = "3";
        fragment.setToProcessCount(toProcess);
        assertEquals(fragment.toProcessButton.getValue(), toProcess);
    }

    @Test
    public void navigateToOrdersList_configuration1() {
        OrderFilterList filters = OrderFilterList.create(Collections.<OrderFilter>singletonList(StatusFilter.create(StatusFilter.Status.TO_PROCESS)));
        fragment.navigateToOrdersList(filters, false);

        ShadowActivity shadowActivity = shadowOf(fragment.getActivity());
        Intent next = shadowActivity.getNextStartedActivity();

        assertEquals(next.getParcelableExtra(OrdersListActivity.EXTRA_ORDER_FILTERS), filters);
        assertEquals(next.getBooleanExtra(OrdersListActivity.EXTRA_PROCESS_ALL, false), false);
    }

    @Test
    public void navigateToOrdersList_configuration2() {
        OrderFilterList filters = OrderFilterList.create(Collections.<OrderFilter>singletonList(StatusFilter.create(StatusFilter.Status.ALL)));
        fragment.navigateToOrdersList(filters, true);

        ShadowActivity shadowActivity = shadowOf(fragment.getActivity());
        Intent next = shadowActivity.getNextStartedActivity();

        assertEquals(next.getParcelableExtra(OrdersListActivity.EXTRA_ORDER_FILTERS), filters);
        assertEquals(next.getBooleanExtra(OrdersListActivity.EXTRA_PROCESS_ALL, false), true);
    }

    @Test
    public void showGetIdsDialog_correctTitleAndMessage() {
        fragment.showGetIdsDialog(listener);

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(dialog);

        assertEquals(shadowDialog.getTitle().toString(), fragment.getString(R.string.dashboard_orders_find_dialog_title));
        assertEquals(shadowDialog.getMessage().toString(), fragment.getString(R.string.dashboard_orders_find_dialog_message));
    }

    @Test
    public void showGetIdsDialog_callsListenerSuccess() {
        fragment.showGetIdsDialog(listener);

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(dialog);

        EditText editText = (EditText) shadowDialog.getView();
        editText.setText("1,2,3");

        HashSet<Integer> ids = new HashSet<>(3);
        ids.addAll(Arrays.asList(1,2,3));

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
        verify(listener).onValues(ids);
    }

    @Test
    public void showGetIdsDialog_callsListenerError() {
        fragment.showGetIdsDialog(listener);

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(dialog);

        EditText editText = (EditText) shadowDialog.getView();
        editText.setText("1,B,3");
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

        verify(listener).onError();
    }


    @Test
    public void showErrorGettingIds() {
        fragment.showErrorGettingIds();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(dialog);

        assertEquals(shadowDialog.getTitle().toString(), fragment.getString(R.string.dashboard_orders_find_dialog_error_title));
        assertEquals(shadowDialog.getMessage().toString(), fragment.getString(R.string.dashboard_orders_find_dialog_erro_message));
    }

    @Test
    public void toProcessButton() {
        fragment.toProcessButton();
        verify(presenter).onToProcessButtonClicked();
    }

    @Test
    public void processedButton() {
        fragment.processedButton();
        verify(presenter).onProcessedButtonClicked();
    }

    @Test
    public void allButton() {
        fragment.allButton();
        verify(presenter).onAllButtonClicked();
    }

    @Test
    public void findButton() {
        fragment.findButton();
        verify(presenter).onFindButtonClicked();
    }


}