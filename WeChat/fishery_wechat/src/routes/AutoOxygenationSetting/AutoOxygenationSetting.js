import React from 'react';
import './autoOxygenationSetting.less';
import { Flex, Toast, Icon, DatePicker, List } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import addImg from '../../img/add.png'

const nowTimeStamp = Date.now();
const now = new Date(nowTimeStamp);
// GMT is not currently observed in the UK. So use UTC now.
const utcNow = new Date(now.getTime() + (now.getTimezoneOffset() * 60000));

// Make sure that in `time` mode, the maxDate and minDate are within one day.
let minDate = new Date(nowTimeStamp - 1e7);
const maxDate = new Date(nowTimeStamp + 1e7);
// console.log(minDate, maxDate);
if (minDate.getDate() !== maxDate.getDate()) {
  // set the minDate to the 0 of maxDate
  minDate = new Date(maxDate.getFullYear(), maxDate.getMonth(), maxDate.getDate());
}

function formatDate(date) {
  /* eslint no-confusing-arrow: 0 */
  const pad = n => n < 10 ? `0${n}` : n;
  const dateStr = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
  const timeStr = `${pad(date.getHours())}:${pad(date.getMinutes())}`;
  return `${dateStr} ${timeStr}`;
}

// If not using `List.Item` as children
// The `onClick / extra` props need to be processed within the component
const CustomChildren = ({ extra, onClick, children }) => (
  <div
    onClick={onClick}
    style={{ backgroundColor: '#fff', height: '45px', lineHeight: '45px', padding: '0 15px' }}
  >
    {children}
    <span style={{ float: 'right', color: '#888' }}>{extra}</span>
  </div>
);

class AutoOrxygenationSetting extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            form: {
            },
            timeSection: [],
            time:''
        }
    }

    

    handlaInput = (type, value) => {
        if (type) {
            const form = this.state.form;
            switch (type) {
                case 'name':
                    form['name'] = value;
                    this.setState({
                        form: form
                    })
                    break;
                case 'phone':
                    form['phone'] = value.replace(/\D/g, '');
                    this.setState({
                        form: form
                    })
                    break;
                case 'years':
                    form['years'] = value.replace(/\D/g, '');
                    this.setState({
                        form: form
                    })
                    break;
                default: break;
            }
        }
    }

    saveInfo = () => {

    }

    addTimeSection = () => {

    }

    render() {
        const times = this.state.timeSection
        let timeSection = times.map((item, index) => {

        })
        return <div className='oxygen-set-bg' style={{ height: window.document.body.clientHeight }} >
            <div style={{ marginBottom: '.74rem' }}>
                <div className='input-line'>
                    <div className='left-item'>
                        控制器1<span>(增氧机1号)</span>
                    </div>
                    <div className='right-item'>
                        10.26mg/L
                    </div>
                </div>
                <div className='input-line'>
                    <div className='left-item'>
                        溶氧下限：
                    </div>
                    <div className='right-item'>
                        <input maxLength='11' className='input' value={this.state.form.phone} onChange={(e) => { this.handlaInput('phone', e.target.value) }} />
                        mg/L
                    </div>
                </div>
                <div className='input-line'>
                    <div className='left-item'>
                        溶氧上限：
                    </div>
                    <div className='right-item'>
                        <input maxLength='11' className='input' value={this.state.form.phone} onChange={(e) => { this.handlaInput('phone', e.target.value) }} />
                        mg/L
                    </div>
                </div>
                <div className='input-line'>
                    <div className='left-item'>
                        溶氧高限：
                    </div>
                    <div className='right-item'>
                        <input className='input' value={this.state.form.years} onChange={(e) => { this.handlaInput('years', e.target.value) }} />
                        mg/L
                    </div>
                </div>
                <div className='input-line'>
                    <div className='left-item'>
                        定时增氧：
                    </div>
                    <div className='right-item'>
                        <div className='input'>
                            <DatePicker
                                mode="time"
                                minuteStep={5}
                                value={this.state.time}
                                onChange={time => this.setState({ time })}

                            >
                                <span>111</span>
                            </DatePicker>
                        </div>
                    </div>
                </div>
                <div className='add-block' onClick={this.addTimeSection} >
                    定时<img className='add-img' src={addImg} />
                </div>

            </div>

            <div className='buttons'>
                <div className='left-button' onClick={() => { this.saveInfo() }} >
                    从 设 备 获 取
            </div>
                <div className='right-button' onClick={() => { this.saveInfo() }} >
                    保 存 到 设 备
            </div>

            </div>

        </div>
    }
}

export default withRouter(AutoOrxygenationSetting);
