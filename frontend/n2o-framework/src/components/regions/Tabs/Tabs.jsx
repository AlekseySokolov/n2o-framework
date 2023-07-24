import React from 'react'
import PropTypes from 'prop-types'
import classNames from 'classnames'
import find from 'lodash/find'
import isEmpty from 'lodash/isEmpty'
import get from 'lodash/get'

import { Group as TabsGroup } from '@i-novus/n2o-components/lib/inputs/RadioGroup/tabs/Group'

import { TabContent } from './TabContent'

/**
 * Компонент контейнера табов
 * @reactProps {string} className - css-класс
 * @reactProps {string} navClassName - css-класс для нава
 * @reactProps {string} maxHeight - кастом max-высота контента таба, фиксация табов
 * @reactProps {function} onChangeActive
 * @reactProps {function} hideSingleTab - скрывать / не скрывать навигацию таба, если он единственный
 * @reactProps {node} children - элемент потомок компонента Tabs
 * @reactProps {boolean} scrollbar - спрятать scrollbar (default = true)
 * @example
 * <Tabs>
 * {
 *   containers.map((cnt) =>
 *     <Tab id={cnt.id}
 *          title={cnt.name || cnt.id}
 *          active={cnt.opened}>
 *       <WidgetFactory containerId={cnt.id} pageId={cnt.pageId} {...cnt.widget} />
 *     </Tab>
 *   )
 * }
 * </Tabs>
 */

class Tabs extends React.Component {
    componentDidUpdate(prevProps) {
        const { onChangeActive, children, activeId } = this.props

        const getActiveEntityVisibility = (children) => {
            const activeEntityMeta = find(children, child => get(child, 'props.id') === activeId)

            return get(activeEntityMeta, 'props.visible')
        }

        const activeEntityVisibility = getActiveEntityVisibility(children)

        const activeEntityVisibilityChanged = activeEntityVisibility !== getActiveEntityVisibility(prevProps.children)

        if (activeEntityVisibilityChanged && !activeEntityVisibility && !isEmpty(prevProps.children)) {
            const firstVisibleTab = find(children, child => child.props.visible)

            onChangeActive(get(firstVisibleTab, 'key'), get(firstVisibleTab, 'key', prevProps.activeId))
        }
    }

    /**
    * установка активного таба
    * @param id
    */
    handleChangeActive = (event) => {
        const { onChangeActive } = this.props

        onChangeActive({}, event?.target?.value, this.defaultOpenedId)
    };

    /**
    * getter для айдишника активного таба
    * @return {Array|*}
    */
    get defaultOpenedId() {
        const { children, activeId } = this.props

        if (activeId) {
            return activeId
        }

        const foundChild = find(React.Children.toArray(children), child => child.props.active)

        return foundChild && foundChild.props.id
    }

    render() {
        const {
            className,
            navClassName,
            children,
            hideSingleTab,
            dependencyVisible,
            scrollbar,
            maxHeight,
        } = this.props

        const activeId = this.defaultOpenedId

        const tabContentStyle = maxHeight ? { maxHeight } : {}

        const tabNavItems = React.Children.map(children, (child) => {
            const { id, title, icon, disabled, visible, invalid } = child.props

            const hasSingleVisibleTab = children.filter(child => child.props.visible).length === 1

            if (
                (hasSingleVisibleTab && hideSingleTab) ||
                !dependencyVisible ||
                !visible
            ) {
                return null
            }

            return {
                className: 'n2o-tabs-nav-item',
                value: id,
                disabled,
                invalid,
                tooltip: invalid && 'Содержит невалидные поля',
                label: (
                    <>
                        {icon && <span className={icon} />}
                        {' '}
                        {title}
                    </>
                ),
            }
        })

        return (
            <div
                className={classNames('n2o-nav-tabs-container', {
                    [className]: className,
                    fixed: maxHeight,
                })}
            >
                {!isEmpty(tabNavItems) && (
                    <div
                        className={classNames('n2o-tabs-nav', {
                            'n2o-nav-tabs_tabs-fixed': maxHeight,
                        })}
                    >
                        <TabsGroup
                            className={classNames('n2o-nav-tabs__tabs-list', {
                                navClassName,
                            })}
                            options={tabNavItems}
                            inline
                            onChange={this.handleChangeActive}
                            value={activeId}
                        />
                    </div>
                )}
                <TabContent
                    className={classNames('n2o-tab-content__container', {
                        visible: dependencyVisible,
                        fixed: maxHeight,
                        'tab-content_fixed': maxHeight,
                        'tab-content_no-scrollbar': scrollbar === false,
                    })}
                    style={tabContentStyle}
                >
                    {React.Children.map(children, child => React.cloneElement(child, {
                        active: activeId === child.props.id,
                        fixed: !!maxHeight,
                    }))}
                </TabContent>
            </div>
        )
    }
}

Tabs.propTypes = {
    /**
     * Класс
     */
    className: PropTypes.string,
    /**
     * Класс навигации
     */
    navClassName: PropTypes.string,
    /**
     * Callback на изменение активного таба
     */
    onChangeActive: PropTypes.func,
    children: PropTypes.node,
    /**
     * спрятать/не прятать scrollbar
     */
    scrollbar: PropTypes.bool,
    activeId: PropTypes.string,
    maxHeight: PropTypes.number,
    hideSingleTab: PropTypes.bool,
    dependencyVisible: PropTypes.any,
}

Tabs.defaultProps = {
    onChangeActive: () => {},
    scrollbar: false,
}

export default Tabs
