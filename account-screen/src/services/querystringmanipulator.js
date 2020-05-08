export const getQueryStringAsJson = (location) => {
    if(location === null || location === undefined || location.search === null ||
         location.search === undefined) {
        return null;
    }
    let queryArray = location.search.substring(1).split("&");
    if(queryArray === null || queryArray === undefined || queryArray.length < 1) {
        return null;
    }
    const newParams = {};
    queryArray.forEach((term) => {
        const pair = term.split("=");
        if(pair.length === 2) {
            newParams[pair[0]] = pair[1].replace(/[+]/g, " ");
        }
    })
    return newParams;
}
