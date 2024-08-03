import React, { Component } from 'react';
import { Grid, TextField, Button, Snackbar, Alert, Card, CardContent, Typography, AlertColor, Box, CircularProgress } from '@mui/material';
import axios from 'axios';
import './NewsLetterSignUp.css';

interface IProps {
    webAPI: String;
}

type State = {
    email: string;
    feedback: { open: boolean, message: string, severity: AlertColor };
    emailError: string;
    usersData: [];
    loading: boolean;
};

class NewsLetterSignUp extends Component<IProps, State> {
    constructor(props: IProps) {
        super(props);
        this.state = {
            email: '',
            feedback: {
                open: false,
                message: '',
                severity: 'success'
            },
            emailError: '',
            usersData: [],
            loading: false
        };
    }

    public componentDidMount(): void {
        axios.get(this.props.webAPI + 'users')
            .then(res => {
                const persons = res.data.data;
                this.setState({ usersData: persons });
            }).catch(error => {
                console.log('error', error);
            })
        console.log('data', this.state.usersData);
    }

    private validateEmail() {
        let emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (this.state.email === '') {
            this.setState({ emailError: 'Please enter the email to Subscribe/Unsubscribe' });
            return false;
        }
        if (!emailRegex.test(this.state.email)) {
            this.setState({ emailError: 'Invalid email address.' });
            return false;
        }
        return true;
    }

    private handleSubmit = async (e: React.FormEvent, action: string) => {
        e.preventDefault();

        if (!this.validateEmail()) {
            return;
        }
        this.setState({ loading: true });

        const { email } = this.state;
        try {
            const data = { email };
            const response = await axios.post(`${this.props.webAPI}${action}`, data, {
                headers: { 'Content-Type': 'application/json' },
            });
            if (response.data.success) {
                this.setState({
                    feedback: { open: true, message: response.data.message, severity: 'success' },
                    loading: false
                });
            }
        } catch (error: any) {
            if (error.response && error.response.data) {
                this.setState({
                    feedback: { open: true, message: error.response.data.message, severity: 'error' },
                    loading: false
                });
            } else {
                this.setState({
                    feedback: { open: true, message: 'An unexpected error occurred.', severity: 'error' },
                    loading: false
                });
            }
        }
    };

    render() {
        const { email, feedback, emailError, loading } = this.state;

        return (
            <div className="newsletter-signup">
                <Grid container spacing={2} justifyContent="center">
                    <Grid item xs={12} sm={8} md={6} lg={4}>
                        <Card className="card">
                            <CardContent>
                                <Typography variant="h5" component="div" className="card-title">
                                    Newsletter Sign-Up
                                </Typography>
                                <form>
                                    <TextField
                                        fullWidth
                                        label="Email Address"
                                        variant="outlined"
                                        value={email}
                                        onChange={(e) => this.setState({ email: e.target.value, emailError: '' })}
                                        error={!!emailError}
                                        helperText={emailError}
                                        className="textfield"
                                    />
                                    <Box display="flex" justifyContent="flex-end" alignItems="center" mt={2}>
                                        {loading ? (
                                            <CircularProgress />
                                        ) : (
                                            <>
                                                <Button
                                                    variant="contained"
                                                    color="primary"
                                                    type="submit"
                                                    className="button"
                                                    onClick={(e) => this.handleSubmit(e, 'subscribe')}
                                                    style={{ marginRight: '10px' }}
                                                >
                                                    Subscribe
                                                </Button>
                                                <Button
                                                    variant="contained"
                                                    color="primary"
                                                    type="submit"
                                                    className="button"
                                                    onClick={(e) => this.handleSubmit(e, 'unsubscribe')}
                                                >
                                                    Unsubscribe
                                                </Button>
                                            </>
                                        )}
                                    </Box>
                                </form>
                            </CardContent>
                        </Card>
                        <Snackbar
                            open={feedback.open}
                            autoHideDuration={6000}
                            onClose={() => this.setState({ feedback: { ...feedback, open: false } })}
                        >
                            <Alert onClose={() => this.setState({ feedback: { ...feedback, open: false } })} severity={feedback.severity}>
                                {this.state.feedback.message}
                            </Alert>
                        </Snackbar>
                    </Grid>
                </Grid>
            </div>
        );
    }
}

export default NewsLetterSignUp;
