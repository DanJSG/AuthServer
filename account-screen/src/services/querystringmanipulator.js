export const getQueryStringAsJson = (location) => {
    if (location === null || location === undefined || location.search === null ||
        location.search === undefined) {
        return null;
    }
    let queryArray = location.search.substring(1).split("&");
    if (queryArray === null || queryArray === undefined || queryArray.length < 1) {
        return null;
    }
    const newParams = {};
    queryArray.forEach((term) => {
        const pair = term.split("=");
        if (pair.length === 2) {
            newParams[pair[0]] = pair[1].replace(/[+]/g, " ");
        }
    })
    return newParams;
}

export const buildQueryStringFromObject = (basePath, params) => {
    const paramNames = Object.keys(params);
    let path = basePath;
    for (let i = 0; i < paramNames.length; i++) {
        const conjunction = i < 1 ? "?" : "&";
        path += conjunction + paramNames[i] + "=" + params[paramNames[i]];
    }
    path = path.replace(/[ ]/, "+");
    return path;
}
