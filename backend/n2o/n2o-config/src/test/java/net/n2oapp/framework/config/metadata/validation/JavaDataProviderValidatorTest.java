package net.n2oapp.framework.config.metadata.validation;

import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.N2oDataProvidersPack;
import net.n2oapp.framework.config.metadata.pack.N2oObjectsPack;
import net.n2oapp.framework.config.metadata.pack.N2oQueriesPack;
import net.n2oapp.framework.config.metadata.validation.standard.invocation.JavaDataProviderValidator;
import net.n2oapp.framework.config.metadata.validation.standard.object.ObjectValidator;
import net.n2oapp.framework.config.metadata.validation.standard.query.QueryValidator;
import net.n2oapp.framework.config.test.SourceValidationTestBase;
import org.junit.jupiter.api.BeforeEach;

/**
 * Тестирование валидации java provider
 */
public class JavaDataProviderValidatorTest extends SourceValidationTestBase {

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oObjectsPack(), new N2oDataProvidersPack(), new N2oQueriesPack());
        builder.validators(new ObjectValidator(), new QueryValidator(), new JavaDataProviderValidator());
    }
}
