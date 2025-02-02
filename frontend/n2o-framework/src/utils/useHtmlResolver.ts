import isEmpty from 'lodash/isEmpty'

import { Model } from '../components/widgets/Form/fields/MarkdownField/helpers'

import evalExpression, { parseExpression } from './evalExpression'

export const useHtmlResolver = (html: string, model: Model) => {
    if (!html) {
        return null
    }

    const parsedExpression = parseExpression(html)

    if (!parsedExpression) {
        return html
    }

    if (isEmpty(model)) {
        return null
    }

    return evalExpression(parsedExpression.replace(/\n/g, '\\n'), model)
}
