package net.n2oapp.framework.config.metadata.compile.widget;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oCards;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.cell.N2oCell;
import net.n2oapp.framework.api.metadata.local.CompiledObject;
import net.n2oapp.framework.api.metadata.meta.widget.Cards;
import net.n2oapp.framework.config.metadata.compile.ComponentScope;
import net.n2oapp.framework.config.metadata.compile.IndexScope;
import net.n2oapp.framework.config.metadata.compile.PageRoutesScope;
import net.n2oapp.framework.config.metadata.compile.ParentRouteScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

/**
 * Компиляция виджета Карточки
 */
@Component
public class CardsCompiler extends BaseListWidgetCompiler<Cards, N2oCards> {
    @Override
    protected String getPropertyWidgetSrc() {
        return "n2o.api.widget.cards.src";
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oCards.class;
    }

    @Override
    public Cards compile(N2oCards source, CompileContext<?, ?> context, CompileProcessor p) {
        Cards cards = new Cards();
        CompiledObject object = getObject(source, p);
        compileWidget(cards, source, context, p, object);
        ParentRouteScope widgetRoute = initWidgetRouteScope(cards, context, p);
        PageRoutesScope pageRoutesScope = p.getScope(PageRoutesScope.class);
        if (pageRoutesScope != null) {
            pageRoutesScope.put(cards.getId(), widgetRoute);
        }
        compileDataProviderAndRoutes(cards, source, context, p, null, widgetRoute, null, null, object);
        WidgetScope widgetScope = new WidgetScope();
        widgetScope.setWidgetId(source.getId());
        widgetScope.setQueryId(source.getQueryId());
        widgetScope.setClientWidgetId(cards.getId());
        MetaActions widgetActions = new MetaActions();
        compileToolbarAndAction(cards, source, context, p, widgetScope, widgetRoute, widgetActions, object, null);

        cards.setCards(compileCols(source.getContent(), context, p, object, widgetScope, widgetActions));
        cards.setAlign(p.cast(source.getAlign(), p.resolve(property("n2o.api.widget.cards.align"), Cards.Position.class)));
        cards.setPaging(compilePaging(source, p.resolve(property("n2o.api.widget.cards.size"), Integer.class), p));
        return cards;
    }

    private Cards.Card[] compileCols(N2oCards.Col[] source, CompileContext<?, ?> context, CompileProcessor p,
                                     CompiledObject object, WidgetScope widgetScope, MetaActions widgetActions) {
        List<Cards.Card> cards = new ArrayList<>(source.length);
        for (N2oCards.Col col : source) {
            Cards.Card card = new Cards.Card();
            card.setSize(col.getSize());
            card.setContent(compileBlock(col.getBlocks(), context, p, object, widgetScope, widgetActions));
            cards.add(card);
        }
        return cards.toArray(new Cards.Card[0]);
    }

    private N2oCell[] compileBlock(N2oCards.Block[] source, CompileContext<?, ?> context, CompileProcessor p,
                                   Object... scopes) {
        List<N2oCell> cells = new ArrayList<>(source.length);
        for (N2oCards.Block block : source) {
            block.setId(p.cast(block.getId(), block.getTextFieldId()));
            cells.add(p.compile(block.getComponent(), context, p, new IndexScope(), new ComponentScope(block), scopes));
        }
        return cells.toArray(new N2oCell[0]);
    }
}
