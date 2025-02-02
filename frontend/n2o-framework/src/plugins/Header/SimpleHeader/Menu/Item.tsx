import React from 'react'
import { ButtonDropdownProps } from 'reactstrap'

import NavItemContainer from '../NavItemContainer'
import { Item as ItemProps, Common, ContextItemCommon } from '../../../CommonMenuTypes'

export interface Dropdown extends Common {
    items: ItemProps[]
    nested?: boolean
    direction?: ButtonDropdownProps['direction']
    recursiveClose?: boolean
    onItemClick?(): void
    level?: number
    from?: 'HEADER' | 'SIDEBAR'
}

export interface DropdownContextItem extends ContextItemCommon {
    item: Dropdown
}

export function Item(props: ItemProps) {
    const { href, id, pathname, datasource, datasources } = props
    const active = href ? pathname.includes(href) : false

    return (
        <NavItemContainer
            itemProps={props}
            active={active}
            datasource={datasource}
            id={id}
            datasources={datasources}
            visible
        />
    )
}
