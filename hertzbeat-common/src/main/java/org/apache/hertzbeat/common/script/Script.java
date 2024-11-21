package org.apache.hertzbeat.common.script;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Script entity class
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Script {

    private Long id;

    private String type;

    private String content;
}
