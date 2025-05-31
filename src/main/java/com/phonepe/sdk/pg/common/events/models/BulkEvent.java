package com.phonepe.sdk.pg.common.events.models;

import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.events.Constants;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulkEvent {

    private List<BaseEvent> events;
    @Default
    private String source = Constants.SOURCE;
    @Default
    private String clientVersion = Headers.SDK_TYPE + ":" + Headers.SDK_VERSION;
}
