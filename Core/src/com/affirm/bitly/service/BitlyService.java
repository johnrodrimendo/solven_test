package com.affirm.bitly.service;

import com.affirm.bitly.model.ShortLinkRequest;
import com.affirm.bitly.model.ShortLinkResult;

public interface BitlyService {

    ShortLinkResult createShortLink(ShortLinkRequest shortLinkRequest);

}
