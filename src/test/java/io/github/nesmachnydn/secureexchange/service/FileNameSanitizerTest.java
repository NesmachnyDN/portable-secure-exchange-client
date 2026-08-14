package io.github.nesmachnydn.secureexchange.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileNameSanitizerTest {
    private final FileNameSanitizer sanitizer = new FileNameSanitizer();

    @Test
    void stripsPathComponentsAndUnsafeCharacters() {
        assertThat(sanitizer.sanitize("../../client/report:final.pdf")).isEqualTo("report_final.pdf");
        assertThat(sanitizer.sanitize("C:\\temp\\report.pdf")).isEqualTo("report.pdf");
    }

    @Test
    void rejectsBlankNames() {
        assertThatThrownBy(() -> sanitizer.sanitize("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
