package io.freshcabin.demo;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import ai.pleos.playground.vehicle.Vehicle;
import ai.pleos.playground.vehicle.api.HVAC;
import ai.pleos.playground.vehicle.api.model.area.SeatArea;
import ai.pleos.playground.vehicle.constant.area.SeatValue;
import java.util.Arrays;
import java.util.List;
import kotlin.Unit;

public final class PleosActivity extends MainActivity {
    private static final String CLIMATE_PERMISSION = "pleos.car.permission.CAR_CLIMATE";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Vehicle vehicle;
    private TextView status;
    private Button refresh;
    private int requestNumber;
    private Runnable timeout;

    @Override
    protected String prepareHtml(String html) {
        return html.replace("<body>", "<body class=\"pleos-vehicle\">");
    }

    @Override
    protected void showBrowser(WebView browser) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(18), dp(4), dp(12), dp(4));
        row.setBackgroundColor(Color.rgb(19, 36, 40));
        status = new TextView(this);
        status.setTextColor(Color.rgb(214, 245, 236));
        status.setTextSize(15);
        status.setAccessibilityLiveRegion(android.view.View.ACCESSIBILITY_LIVE_REGION_POLITE);
        status.setText("Pleos SDK · 공조 상태 조회 대기 · 아래 주행은 시뮬레이션");
        row.addView(status, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        refresh = new Button(this);
        refresh.setText("Pleos 공조 조회");
        refresh.setAllCaps(false);
        refresh.setOnClickListener(ignored -> readClimate());
        row.addView(refresh);
        layout.addView(row);
        layout.addView(browser, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(layout);
    }

    private void readClimate() {
        if (checkSelfPermission(CLIMATE_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            status.setText("Pleos 공조 조회 권한 없음 · Pleos Connect 에뮬레이터에서 실행하세요");
            return;
        }

        final int request = ++requestNumber;
        refresh.setEnabled(false);
        status.setText("Pleos SDK · 공조 순환 지원 영역 조회 중");
        timeout = () -> finish(request, "Pleos SDK 응답 시간 초과 · Vehicle Service를 확인하세요");
        handler.postDelayed(timeout, 6500);
        try {
            if (vehicle == null) {
                Vehicle client = new Vehicle(getApplicationContext());
                client.initialize();
                vehicle = client;
            }
            HVAC climate = vehicle.getHVAC();
            climate.checkHvacRecirculationCapabilities(
                    (config, areas) -> {
                        handler.post(() -> readArea(request, climate, areas));
                        return Unit.INSTANCE;
                    },
                    failure -> failed(request, "순환 지원 확인", failure));
        } catch (RuntimeException failure) {
            failed(request, "SDK 초기화", failure);
        }
    }

    private void readArea(int request, HVAC climate, List<SeatArea> areas) {
        if (request != requestNumber || timeout == null) return;
        if (areas == null || areas.isEmpty()) {
            finish(request, "Pleos SDK · 이 환경에서 지원하는 공조 순환 영역 없음");
            return;
        }
        SeatArea area = areas.stream()
                .filter(candidate -> Arrays.asList(candidate.getValues()).contains(SeatValue.ROW_1_LEFT))
                .findFirst().orElse(areas.get(0));
        try {
            climate.getHvacRecirculationState(area, recirculating -> {
                handler.post(() -> {
                    String message = recirculating == null
                            ? "Pleos SDK · 공조 순환 값 없음"
                            : "Pleos SDK 조회 · " + (recirculating ? "내기순환" : "외기순환") + " · 읽기 전용";
                    finish(request, message);
                });
                return Unit.INSTANCE;
            }, failure -> failed(request, "공조 조회", failure));
        } catch (RuntimeException failure) {
            failed(request, "공조 조회", failure);
        }
    }

    private Unit failed(int request, String operation, Exception failure) {
        handler.post(() -> finish(request, "Pleos SDK · " + operation + " 실패 ("
                + failure.getClass().getSimpleName() + ")"));
        return Unit.INSTANCE;
    }

    private void finish(int request, String message) {
        if (request != requestNumber || timeout == null) return;
        handler.removeCallbacks(timeout);
        timeout = null;
        status.setText(message);
        refresh.setEnabled(true);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        ++requestNumber;
        handler.removeCallbacksAndMessages(null);
        try {
            if (vehicle != null) vehicle.release();
        } finally {
            super.onDestroy();
        }
    }
}
