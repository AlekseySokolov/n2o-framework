package net.n2oapp.framework.engine.processor;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.criteria.filters.FilterType;
import net.n2oapp.framework.api.context.ContextProcessor;
import net.n2oapp.framework.api.criteria.N2oPreparedCriteria;
import net.n2oapp.framework.api.criteria.Restriction;
import net.n2oapp.framework.api.metadata.global.dao.query.N2oQuery;
import net.n2oapp.framework.api.metadata.local.CompiledQuery;
import net.n2oapp.framework.api.register.MetaType;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.compile.pipeline.N2oEnvironment;
import net.n2oapp.framework.config.compile.pipeline.operation.BindOperation;
import net.n2oapp.framework.config.compile.pipeline.operation.CompileOperation;
import net.n2oapp.framework.config.compile.pipeline.operation.ReadOperation;
import net.n2oapp.framework.config.io.dataprovider.JavaDataProviderIOv1;
import net.n2oapp.framework.config.io.dataprovider.TestDataProviderIOv1;
import net.n2oapp.framework.config.io.query.QueryElementIOv4;
import net.n2oapp.framework.config.io.query.QueryElementIOv5;
import net.n2oapp.framework.config.metadata.compile.context.QueryContext;
import net.n2oapp.framework.config.metadata.compile.object.N2oObjectCompiler;
import net.n2oapp.framework.config.metadata.compile.query.N2oQueryCompiler;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.selective.SelectiveMetadataLoader;
import net.n2oapp.framework.engine.data.N2oInvocationFactory;
import net.n2oapp.framework.engine.data.N2oQueryExceptionHandler;
import net.n2oapp.framework.engine.data.N2oQueryProcessor;
import net.n2oapp.framework.engine.data.java.JavaDataProviderEngine;
import net.n2oapp.framework.engine.data.json.TestDataProviderEngine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Тестирование процессора запросов
 */
public class QueryProcessorTest {
    private N2oQueryProcessor queryProcessor;
    private N2oApplicationBuilder builder;
    private N2oInvocationFactory factory;

    @Before
    public void setUp() throws Exception {
        ContextProcessor contextProcessor = mock(ContextProcessor.class);
        factory = mock(N2oInvocationFactory.class);
        when(contextProcessor.resolve(anyString())).then((Answer) invocation -> invocation.getArguments()[0]);
        when(contextProcessor.resolve(anyInt())).then((Answer) invocation -> invocation.getArguments()[0]);
        when(contextProcessor.resolve(anyBoolean())).then((Answer) invocation -> invocation.getArguments()[0]);
        queryProcessor = new N2oQueryProcessor(factory, new N2oQueryExceptionHandler());
        N2oEnvironment environment = new N2oEnvironment();
        environment.setContextProcessor(contextProcessor);
        environment.setReadPipelineFunction(p -> p.read());
        environment.setReadCompilePipelineFunction(p -> p.read().compile());
        environment.setCompilePipelineFunction(p -> p.compile());
        queryProcessor.setEnvironment(environment);
        builder = new N2oApplicationBuilder(environment)
                .types(new MetaType("query", N2oQuery.class))
                .loaders(new SelectiveMetadataLoader()
                        .add(new QueryElementIOv4())
                        .add(new QueryElementIOv5())
                        .add(new TestDataProviderIOv1())
                        .add(new JavaDataProviderIOv1()))
                .operations(new ReadOperation(), new CompileOperation(), new BindOperation())
                .compilers(new N2oQueryCompiler(), new N2oObjectCompiler())
                .sources(new CompileInfo("net/n2oapp/framework/engine/processor/testQueryProcessorV4Java.query.xml"),
                        new CompileInfo("net/n2oapp/framework/engine/processor/testQueryProcessorV4JavaMapping.query.xml"),
                        new CompileInfo("net/n2oapp/framework/engine/processor/testQueryProcessorUnique.query.xml"),
                        new CompileInfo("net/n2oapp/framework/engine/processor/testQueryProcessorNorm.query.xml"),
                        new CompileInfo("net/n2oapp/framework/engine/processor/testQueryProcessorRequiredFilter.query.xml"),
                        new CompileInfo("net/n2oapp/framework/engine/processor/testReferenceFields.query.xml"),
                        new CompileInfo("net/n2oapp/framework/engine/processor/testReferenceFieldMapping.query.xml"));
    }

    @Test
    public void query4Java() {
        when(factory.produce(any())).thenReturn(new JavaDataProviderEngine());
        CompiledQuery query = builder.read().compile().get(new QueryContext("testQueryProcessorV4Java"));

        //case without arguments
        N2oPreparedCriteria criteria = new N2oPreparedCriteria();
        CollectionPage<DataSet> collectionPage = queryProcessor.execute(query, criteria);
        assertThat(collectionPage.getCount(), is(10));
        DataSet dataSet = (DataSet) ((List) collectionPage.getCollection()).get(0);
        assertThat(dataSet.get("id"), is(0));

        //case with primitive
        criteria = new N2oPreparedCriteria();
        criteria.addRestriction(new Restriction("value", "test"));
        criteria.addRestriction(new Restriction("value", "test"));
        collectionPage = queryProcessor.execute(query, criteria);
        assertThat(collectionPage.getCount(), is(10));
        dataSet = (DataSet) ((List) collectionPage.getCollection()).get(0);
        assertThat(dataSet.get("id"), is(0));
        assertThat(dataSet.get("value"), is("test"));

        //case with criteria (criteria-api)
        criteria = new N2oPreparedCriteria();
        criteria.addRestriction(new Restriction("name", "test"));
        collectionPage = queryProcessor.execute(query, criteria);
        assertThat(collectionPage.getCount(), is(1));
        dataSet = (DataSet) ((List) collectionPage.getCollection()).get(0);
        assertThat(dataSet.get("id"), is(0));
        assertThat(dataSet.get("name"), is("test"));
    }

    /**
     * Тестирование маппинга аргументов java провайдера с использованием name аргументов, а не через заданный порядок
     */
    @Test
    public void testNameMappingWithArgumentsInvocationProvider() {
        JavaDataProviderEngine javaDataProviderEngine = new JavaDataProviderEngine();
        when(factory.produce(any())).thenReturn(javaDataProviderEngine);

        CompiledQuery query = builder.read().compile().get(new QueryContext("testQueryProcessorV4JavaMapping"));
        N2oPreparedCriteria criteria = new N2oPreparedCriteria();
        criteria.addRestriction(new Restriction("thirdArg", true));
        criteria.addRestriction(new Restriction("firstArg", 123));
        criteria.addRestriction(new Restriction("nameArg", "test"));

        CollectionPage<DataSet> collectionPage = queryProcessor.execute(query, criteria);
        assertThat(collectionPage.getCount(), is(1));
        // Result
        DataSet result = ((List<DataSet>) collectionPage.getCollection()).get(0);
        assertThat(result.get("firstArg"), is(123));
        assertThat(result.get("nameArg"), is("test"));
        assertThat(result.get("thirdArg"), is(true));
    }

    @Test
    public void testCriteriaRestrictionMerge() {
        when(factory.produce(any())).thenReturn(new JavaDataProviderEngine());
        CompiledQuery query = builder.read().compile().get(new QueryContext("testQueryProcessorV4Java"));
        N2oPreparedCriteria criteria = new N2oPreparedCriteria();
        criteria.addRestriction(new Restriction("id", "1", FilterType.eq));
        criteria.addRestriction(new Restriction("id", "45", FilterType.eq));
        CollectionPage<DataSet> collectionPage = queryProcessor.execute(query, criteria);
        assertThat(collectionPage.getCount(), is(0));

        criteria = new N2oPreparedCriteria();
        criteria.addRestriction(new Restriction("name", "test", FilterType.eq));
        criteria.addRestriction(new Restriction("name", "test", FilterType.eq));
        collectionPage = queryProcessor.execute(query, criteria);
        assertThat(collectionPage.getCount(), is(1));

        criteria = new N2oPreparedCriteria();
        criteria.addRestriction(new Restriction("id", "1", FilterType.more));
        criteria.addRestriction(new Restriction("id", "45", FilterType.less));
        collectionPage = queryProcessor.execute(query, criteria);
        assertThat(collectionPage.getCount(), is(10));

        criteria = new N2oPreparedCriteria();
        criteria.addRestriction(new Restriction("id", "0", FilterType.eq));
        criteria.addRestriction(new Restriction("name", "test", FilterType.eq));
        collectionPage = queryProcessor.execute(query, criteria);
        assertThat(collectionPage.getCount(), is(1));
        DataSet dataSet = (DataSet) ((List) collectionPage.getCollection()).get(0);
        assertThat(dataSet.get("id"), is(0));
        assertThat(dataSet.get("name"), is("test"));
    }

    @Test
    public void query4Unique() {
        TestDataProviderEngine testDataprovider = new TestDataProviderEngine();
        when(factory.produce(any())).thenReturn(testDataprovider);
        CompiledQuery query = builder.read().compile().get(new QueryContext("testQueryProcessorUnique"));

        //case unique selection
        N2oPreparedCriteria criteria = new N2oPreparedCriteria();
        criteria.setSize(1);
        criteria.addRestriction(new Restriction("id", 1));
        CollectionPage<DataSet> result = queryProcessor.execute(query, criteria);
        assertThat(result.getCount(), is(1));
        DataSet first = result.getCollection().iterator().next();
        assertThat(first.get("id"), is(1L));

        //case list filter by code selection
        criteria = new N2oPreparedCriteria();
        criteria.setSize(1);
        criteria.addRestriction(new Restriction("code", "test1"));
        result = queryProcessor.execute(query, criteria);
        assertThat(result.getCount(), is(1));
        first = result.getCollection().iterator().next();
        assertThat(first.get("code"), is("test1"));

        //case list any filters selection
        criteria = new N2oPreparedCriteria();
        criteria.setSize(1);
        criteria.addRestriction(new Restriction("type", 10));
        result = queryProcessor.execute(query, criteria);
        assertThat(result.getCount(), is(1));
        first = result.getCollection().iterator().next();
        assertThat(first.get("type"), is(10));
    }

    @Test
    public void query4Normalize() {
        when(factory.produce(any())).thenReturn(new TestDataProviderEngine());
        CompiledQuery query = builder.read().compile().get(new QueryContext("testQueryProcessorNorm"));

        N2oPreparedCriteria criteria = new N2oPreparedCriteria();
        CollectionPage<DataSet> result = queryProcessor.execute(query, criteria);
        DataSet first = result.getCollection().iterator().next();
        assertThat(first.get("normTest"), is(Integer.MAX_VALUE));
        assertThat(query.getFieldsMap().get("normTest").getDefaultValue(), is("defaultValue"));
    }

    @Test
    public void testReferenceFields() {
        when(factory.produce(any())).thenReturn(new TestDataProviderEngine());
        CompiledQuery query = builder.read().compile().get(new QueryContext("testReferenceFields"));

        N2oPreparedCriteria criteria = new N2oPreparedCriteria();
        CollectionPage<DataSet> result = queryProcessor.execute(query, criteria);
        assertThat(result.getCount(), is(4));

        DataSet first = result.getCollection().iterator().next();
        assertThat(first.getLong("id"), is(1L));
        assertThat(first.getString("name"), is("TEST1"));
        assertThat(first.getDataSet("organization").getInteger("code"), is(2));
        assertThat(first.getDataSet("organization").getString("title"), is("org2"));
        assertThat(first.getList("departments").size(), is(2));
        assertThat(((DataSet) first.getList("departments").get(0)).getInteger("id"), is(3));
        assertThat(((DataSet) first.getList("departments").get(0)).getString("name"), is("department3"));
        assertThat(((DataSet) first.getList("departments").get(1)).getInteger("id"), is(4));
        assertThat(((DataSet) first.getList("departments").get(1)).getString("name"), is("department4"));
    }

    @Test
    public void testReferenceFieldMapping() {
        when(factory.produce(any())).thenReturn(new TestDataProviderEngine());
        CompiledQuery query = builder.read().compile().get(new QueryContext("testReferenceFieldMapping"));

        N2oPreparedCriteria criteria = new N2oPreparedCriteria();
        CollectionPage<DataSet> result = queryProcessor.execute(query, criteria);
        assertThat(result.getCount(), is(4));

        DataSet first = result.getCollection().iterator().next();
        assertThat(first.getLong("myId"), is(1L));
        assertThat(first.getString("myName"), is("TEST1"));
        assertThat(first.getDataSet("myOrganization").getInteger("myCode"), is(2));
        assertThat(first.getDataSet("myOrganization").getString("myTitle"), is("org2"));
        assertThat(first.getList("myDepartments").size(), is(2));
        assertThat(((DataSet) first.getList("myDepartments").get(0)).getInteger("myId"), is(3));
        assertThat(((DataSet) first.getList("myDepartments").get(0)).getString("myId"), is("department3"));
        assertThat(((DataSet) first.getList("myDepartments").get(1)).getInteger("myId"), is(4));
        assertThat(((DataSet) first.getList("myDepartments").get(1)).getString("myId"), is("department4"));
    }
}
