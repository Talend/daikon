package org.talend.daikon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PullRequest {

    private final String url;

    private final String display;

}
