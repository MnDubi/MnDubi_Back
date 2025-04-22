package festival.dev.domain.shareTDL.presentation;

import festival.dev.domain.shareTDL.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ShareController {
    private final ShareService shareService;
}
