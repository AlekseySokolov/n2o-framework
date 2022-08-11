import React from 'react'
import { mount } from 'enzyme'
import sinon from 'sinon'

import ModalDialog from './ModalDialog'

const setup = (propsOverride) => {
    const props = {
        visible: true,
    }

    return mount(<ModalDialog {...props} {...propsOverride} />)
}

describe('<ModalDialog />', () => {
    it('компонент должен отрисоваться', () => {
        const wrapper = setup()

        expect(wrapper.find('Modal').exists()).toBeTruthy()
    })

    it('должны подставиться title, text, okLabel, cancelLabel', () => {
        const wrapper = setup({
            title: 'Title',
            text: 'Text',
            ok: { label: 'OK' },
            cancel: { label: 'CANCEL' },
        })

        expect(wrapper.find('ModalHeader').text()).toBe('Title')
        expect(wrapper.find('ModalBody').text()).toBe('Text')
        expect(
            wrapper
                .find('Button')
                .first()
                .text(),
        ).toBe('OK')
        expect(
            wrapper
                .find('Button')
                .last()
                .text(),
        ).toBe('CANCEL')
    })

    it('должны подставиться okColor, cancelColor', () => {
        const wrapper = setup({
            ok: { color: 'info' },
            cancel: { color: 'tertiary' },
        })

        expect(
            wrapper
                .find('Button')
                .first()
                .hasClass('btn-info')
        )
        expect(
            wrapper
                .find('Button')
                .last()
                .hasClass('btn-tertiary')
        )
    })

    it('должны отрендериться кнопки в ином порядке', () => {
        const wrapper = setup({
            reverseButtons: true
        })

        expect(
            wrapper
                .find('ButtonGroup')
                .hasClass('flex-row-reverse')
        )
    })

    it('должен вызываться onConfirm', () => {
        const onConfirm = sinon.spy()
        const wrapper = setup({
            onConfirm,
        })

        wrapper
            .find('Button')
            .first()
            .simulate('click')
        expect(onConfirm.calledOnce).toBeTruthy()
    })

    it('должен вызываться onConfirm', () => {
        const onDeny = sinon.spy()
        const wrapper = setup({
            onDeny,
        })

        wrapper
            .find('Button')
            .last()
            .simulate('click')
        expect(onDeny.calledOnce).toBeTruthy()
    })
})
