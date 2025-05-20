package festival.dev.global.security.oauth;

import festival.dev.global.security.oauth.dto.OAuthCodeInfo;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OAuthCodeStore {
    private final Map<String, OAuthCodeInfo> store = new ConcurrentHashMap<>();

    public void save(String code, OAuthCodeInfo info) {
        store.put(code, info);
        // 자동 만료 처리 (예시: 5분 후 삭제)
        new Timer().schedule(new TimerTask() {
            public void run() {
                store.remove(code);
            }
        }, 5 * 60 * 1000);
    }

    public OAuthCodeInfo get(String code) {
        return store.remove(code); // 1회용
    }
}
