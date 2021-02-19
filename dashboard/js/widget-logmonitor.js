class WidgetLogMonitor {
    static _URI_CONFIGS = '/configs/monitor/logging'
    static _URI_METRICS = '/metrics/logging'

    _base_url = null
    _refresh_interval = null
    _refresh_loop_configs = null
    _refresh_loop_data = null
    _query_s = null

    _last_count_err = 0
    _last_count_warn = 0
    _last_count_info = 0

    
    static refresh_configs(instance, loop=false) {
        fetch(instance._base_url + WidgetLogMonitor._URI_CONFIGS)
            .then(response => response.json())
            .then(r => {
                if(instance._refresh_interval != r.refreshInterval) {
                    //restart data loop
                    instance._refresh_interval = r.refreshInterval
                    clearTimeout(instance._refresh_loop_data)
                    WidgetLogMonitor.refresh_data(instance, true)
                }

                instance._refresh_interval = r.refreshInterval
                WidgetLogMonitor.render_configs(instance)
                
                if(loop) {
                    instance._refresh_loop_configs = setTimeout(function() {
                        WidgetLogMonitor.refresh_configs(instance, true)
                    }, 5000)
                }
                
            })
    }

    static refresh_data(instance, loop=false) {
        fetch(instance._base_url + WidgetLogMonitor._URI_METRICS)
            .then(response => response.json())
            .then(r => {
                instance._last_count_err = r.filter(metric => metric.level=="ERROR")[0].count
                instance._last_count_warn = r.filter(metric => metric.level=="WARNING")[0].count
                instance._last_count_info = r.filter(metric => metric.level=="INFO")[0].count

                WidgetLogMonitor.render_values(instance)
                
                if(loop) {
                    instance._refresh_loop_data = setTimeout(function() {
                        WidgetLogMonitor.refresh_data(instance, true)
                    }, instance._refresh_interval*1000)
                }
            })
    }

    static render_values(instance) {
        try {
            document.querySelector(instance._query_s).getElementsByClassName('log-metric-value')[0].innerHTML = instance._last_count_err
            document.querySelector(instance._query_s).getElementsByClassName('log-metric-value')[1].innerHTML = instance._last_count_warn
            document.querySelector(instance._query_s).getElementsByClassName('log-metric-value')[2].innerHTML = instance._last_count_info
        } catch(ex) { 
            // maybe they've not been rendered yet...
        }
    }

    static render_configs(instance) {
        try {
            document.querySelector(instance._query_s).getElementsByClassName('input-refresh-interval')[0].value = instance._refresh_interval
        } catch(ex) { 
            console.log(ex)
            // maybe it's not been rendered yet...
        }
    }

    static render(instance) {
        document.querySelector(instance._query_s).innerHTML += `
                    <div class="card blue-grey darken-1 widget-logmonitor">
                        <div class="card-content white-text">
                            <span class="card-title">Log Metrics</span>
                            <div class="row mb-0">
                                <div class="col s4 log-metric">
                                    <div class="log-metric-circle border-darkred"></div>
                                    <div>&nbsp;</div>
                                    <div class="log-metric-value">0</div>
                                </div>
                                <div class="col s4 log-metric">
                                    <div class="log-metric-circle border-yellow"></div>
                                    <div>&nbsp;</div>
                                    <div class="log-metric-value">0</div>
                                </div>
                                <div class="col s4 log-metric">
                                    <div class="log-metric-circle border-gray"></div>
                                    <div>&nbsp;</div>
                                    <div class="log-metric-value">0</div>
                                </div>
                            </div>
                            <div class="row mb-0 labels-row">
                                <div class="col s4">ERROR</div>
                                <div class="col s4">WARN</div>
                                <div class="col s4">INFO</div>
                            </div>
                        </div>
                        <div class="card-action text-white">
                            <div class="f-left">
                                <div class="inline-block">Refresh Interval (sec):</div>
                                <input type="text" class="input-refresh-interval"></input>
                            </div>
                            <div class="f-right submit-refresh-interval"><a href="#" class="submit-refresh-interval">Change</a></div>
                        </div>
                    </div>
        `;
        document.querySelector(instance._query_s).querySelector('.submit-refresh-interval a').addEventListener('click', function() {
            var xhr = new XMLHttpRequest();
            xhr.open("POST", instance._base_url + WidgetLogMonitor._URI_CONFIGS, true);
            xhr.setRequestHeader('Content-Type', 'application/json');
            xhr.send(JSON.stringify({
                refreshInterval: document.querySelector(instance._query_s).getElementsByClassName('input-refresh-interval')[0].value
            }));
        });
    }

    constructor(base_url) {
        this._base_url = base_url
    }

    init(query_s) {
        this._query_s = query_s
        WidgetLogMonitor.render(this)
        WidgetLogMonitor.refresh_configs(this, true)
    }


}