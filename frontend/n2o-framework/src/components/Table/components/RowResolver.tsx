import React, { useCallback, useMemo, VFC, MouseEvent } from 'react'

import { RowResolverProps } from '../types/props'
import { Selection } from '../enum'
import { useTableActions } from '../provider/TableActions'
import { useToolbarOverlay } from '../provider/ToolbarOverlay'

import { CellContainer } from './CellContainer'
import { DataRow } from './DataRow'

export const RowResolver: VFC<RowResolverProps> = (props) => {
    const {
        component: RowComponent = DataRow,
        elementAttributes,
        data,
        treeDeepLevel,
        isSelectedRow,
        isFocused,
        cells,
        rowValue,
        isTreeExpanded,
        hasExpandedButton,
        selection,
        hasSecurityAccess,
        click,
        rowIndex,
        ...otherProps
    } = props
    const { setFocusOnRow, onRowClick } = useTableActions()
    const { onShowOverlay, onHideOverlay } = useToolbarOverlay()
    const onClickRowAction = useCallback((data) => {
        onRowClick(data)
    }, [onRowClick])
    const onSelection = useCallback((data) => {
        setFocusOnRow(data.id, data)
    }, [setFocusOnRow])

    const { style, ...otherElementAttributes } = elementAttributes || {}
    const hasSelection = selection !== Selection.None
    const hasRowAction = click && hasSecurityAccess

    const mergedStyle = useMemo(() => ({
        '--deep-level': treeDeepLevel,
        ...style,
    }), [treeDeepLevel, style])

    const onMouseEnter = useCallback((event: MouseEvent) => {
        if (onShowOverlay) {
            onShowOverlay(event, data)
        }
    }, [onShowOverlay, data])

    return (
        <RowComponent
            {...otherProps}
            {...otherElementAttributes}
            rowIndex={rowIndex}
            selection={selection}
            data={data}
            onClick={hasRowAction ? onClickRowAction : undefined}
            onSelection={hasSelection ? onSelection : undefined}
            onMouseEnter={onShowOverlay ? onMouseEnter : undefined}
            onMouseLeave={onHideOverlay}
            style={mergedStyle}
            data-focused={isFocused}
            data-has-click={hasSelection || hasRowAction}
            data-deep-level={treeDeepLevel}
            data-selected={isSelectedRow}
        >
            {cells.map(({
                elementAttributes,
                ...cellProps
            }, index) => (
                <CellContainer
                    key={cellProps.id}
                    cellIndex={index}
                    hasExpandedButton={hasExpandedButton}
                    isSelectedRow={isSelectedRow}
                    model={data}
                    rowValue={rowValue}
                    isTreeExpanded={isTreeExpanded}
                    rowIndex={rowIndex}
                    {...elementAttributes}
                    {...cellProps}
                />
            ))}
        </RowComponent>
    )
}
